package com.cyj.xx.service.Impl;

import com.cyj.xx.Enum.OperationType;
import com.cyj.xx.Enum.TargetType;
import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.ConfirmBanUserDTO;
import com.cyj.xx.dto.CreateSectionDTO;
import com.cyj.xx.dto.UpdataSectionDTO;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.ManageShowSectionDVO;
import com.cyj.xx.dvo.MyFollowSectionDVO;
import com.cyj.xx.dvo.SearchSectionDVO;
import com.cyj.xx.entity.*;
import com.cyj.xx.mapper.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SectionsServiceImpl implements SectionsService {

    @Autowired
    private SectionsMapper sectionsMapper;

    @Autowired
    private SectionFollowsMapper sectionFollowsMapper;

    @Autowired
    private UserSignInMapper userSignInMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BannedUserMapper bannedUserMapper;

    @Autowired
    ModeratorOperationLogMapper moderatorOperationLogMapper;


    /**
     * 热门板块显示
     * @param limit
     * @return
     */

    @Override
    public Result<List<HotSectionsDVO>> HotSection(Integer limit) {
        try{

            String key = "Sec:hot";
            if(redisUtil.exists(key)){

                List<HotSectionsDVO> ll =  redisUtil.GetAll(key);
                log.info("获取HotSections缓存 ===> {}",ll);
                return Result.success(ll);
            }


            PageHelper.startPage(1, 10);
            List<HotSectionsDVO> hotSec = sectionsMapper.PageHotSec();
            log.info("未查询到缓存，执行缓存操作");
            redisUtil.pushAll(key, hotSec,20,TimeUnit.HOURS);

            return Result.success(hotSec);

        }catch (Exception e){
            return Result.error("出现错误");
        }

    }

    /**
     * 关注板块显示
     * @param id
     * @return
     */
    @Override
    public Result<List<MyFollowSectionDVO>> mySection(Long id) {

        if(id == null){
            return Result.error("用户数据错误");
        }

        try{
            log.info("获取缓存数据");
            String key = "Sec:followSection:UserId:"+id+":SecId:";
            Set set =  redisUtil.keys(key+"*");
            List<String> l = new ArrayList<>(set);
            List<MyFollowSectionDVO> ll = new ArrayList<>();
            if(!l.isEmpty()){
                for(String str:l){
                ll.add((MyFollowSectionDVO)redisUtil.get(str));
                }
                return Result.success(ll);
            }

            log.info("缓存数据不存在");
            List<MyFollowSectionDVO> mySec =  sectionsMapper.getFollowMySection(id);


            log.info("未查询到缓存，执行缓存操作");
            for(MyFollowSectionDVO mySecDVO:mySec){
                redisUtil.set(key+mySecDVO.getId(), mySecDVO);
            }


            return Result.success(mySec);

        }catch (Exception e){
            throw new RuntimeException("数据查询错误");
        }


    }


    @Override
    public Result<Section> sectionDetail(Long id) {

        String key = "Sec:sectionDetail:SectionId:"+id;

        try{
            //查询缓存
            log.info ("查询缓存");
            if(redisUtil.exists(key)){
                //存在
                Section sec =  (Section) redisUtil.get(key);
                return Result.success(sec);
            }
            //不存在
            log.info("开始查询板块 当前板块id ===> {}",id);
            Section sec =  sectionsMapper.getSectionById(id);
            //加载缓存
            log.info("开始加载缓存");
            redisUtil.set(key,sec,1,TimeUnit.HOURS);

            return Result.success(sec);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }


    }

    @Override
    public Result<Boolean> isFollowSec(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        String key = "Sec:followSection:UserId:"+userId+":SecId:"+sectionId;
        if(redisUtil.exists(key)){
            return Result.success(true);
        }else {
            return Result.success(false);
        }
    }

    @Override
    @Transactional
    public Result<MyFollowSectionDVO> followSec(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        String key = "Sec:followSection:UserId:"+userId+":SecId:"+sectionId;
        try {

            if (redisUtil.exists(key)) {
                return Result.error("已关注此板块");
            }

            //更新关注表
            sectionFollowsMapper.followSec(userId, sectionId);
            //更新对应板块关注数
            Section sec = sectionsMapper.getSectionById(sectionId);
            sec.setFollowersCount(sec.getFollowersCount() + 1);
            sectionsMapper.UpdateSection(sec);

            //更新签到表
            //检查签到信息是否存在
            UserSignIn u = userSignInMapper.getUserSignIn(sectionId, userId);
            if(u != null){
                u.setIsDeleted(0);
                userSignInMapper.UpdateUserSignIn(u);
            }
            else {
                UserSignIn userSignIn = new UserSignIn();
                userSignIn.setSectionId(sectionId);
                userSignIn.setUserId(userId);
                userSignIn.setStatus(false);
                userSignIn.setContinuousDays(0);
                userSignIn.setCreatedAt(LocalDateTime.now());
                userSignIn.setUpdatedAt(LocalDateTime.now());
                userSignInMapper.addNewUserSignIn(userSignIn);
            }



            //添加缓存
            MyFollowSectionDVO mySecDVO = new MyFollowSectionDVO();
            mySecDVO.setId(sectionId);
            mySecDVO.setImage(sec.getImage());
            mySecDVO.setName(sec.getName());
            redisUtil.set(key, mySecDVO);

            return Result.success(mySecDVO);
        }catch (Exception e){
            throw new RuntimeException("关注失败");
        }
    }

    @Override
    @Transactional
    public Result<Boolean> unfollowSec(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        String key = "Sec:followSection:UserId:"+userId+":SecId:"+sectionId;
        try{
            //删除缓存
            if(redisUtil.exists(key)){
                redisUtil.delete(key);
            }
            //删除关注表
            sectionFollowsMapper.unfollowSec(userId, sectionId);
            //删除签到表
            userSignInMapper.UnUserSignIn(userId,sectionId);
            //更新板块关注数
            Section sec = sectionsMapper.getSectionById(sectionId);
            sec.setFollowersCount(sec.getFollowersCount() - 1);
            sectionsMapper.UpdateSection(sec);
            return Result.success(true);
        }catch (Exception e){
            return Result.error("出现未知错误");
        }
    }

    @Override
    public Result<PageResult> searchSection(String keyword, int page, int size) {
        try{
            PageHelper.startPage(page,size);
            Page<SearchSectionDVO> p =  sectionsMapper.searchSection(keyword);
            PageResult pageResult = new PageResult();
            pageResult.setTotal(p.getTotal());
            pageResult.setRecords(p.getResult());
            return Result.success(pageResult);

        }catch (Exception e){
            return Result.error("查询失败");
        }
    }

    @Override
    public Result<Boolean> createSection(Long userId, CreateSectionDTO createSectionDTO) {
        Section newSection = new Section();
        try{
            newSection.setName(createSectionDTO.getName());
            newSection.setImage(createSectionDTO.getImage());
            newSection.setDescription(createSectionDTO.getDescription());
            newSection.setBackgroundImage(createSectionDTO.getBackgroundImage());
            newSection.setAdminId(BaseContext.getCurrentId());
            sectionsMapper.addNewSection(newSection);
            return Result.success(true);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @Override
    public ManageShowSectionDVO getSection(Long sectionId, Long userId) {
        try{

           Section section = sectionsMapper.getSectionById(sectionId);

           ManageShowSectionDVO msc = ManageShowSectionDVO.builder()
                   .name(section.getName())
                   .image(section.getImage())
                   .description(section.getDescription())
                   .backgroundImage(section.getBackgroundImage())
                   .createdTime(section.getCreatedTime())
                   .followersCount(section.getFollowersCount())
                   .id(section.getId())
                   .postCount(section.getPostCount())
                   .build();

           return msc;

        }catch (Exception e){
            throw new RuntimeException("数据查询错误");
        }

    }

    @Override
    public Result<Boolean> UpdataSection(UpdataSectionDTO updataSectionDTO) {
        try {
            Section section = new Section();
            section.setId(updataSectionDTO.getId());
            section.setImage(updataSectionDTO.getImage());
            section.setDescription(updataSectionDTO.getDescription());
            section.setBackgroundImage(updataSectionDTO.getBackgroundImage());
            sectionsMapper.UpdateSection(section);
            return Result.success(true);

        }catch (Exception e){
            return Result.error("修改失败");
        }
    }

    /**
     * 检查版主身份
     * @param sectionId
     * @param userId
     * @return
     */
    @Override
    public Result<Boolean> isModerator(Long sectionId, Long userId) {
        log.info("开始检查版主身份== sectionId->{} == userId->{}",sectionId,userId);
        try {
            Section section =  sectionsMapper.getSectionById(sectionId);
            if(section.getAdminId().equals(userId)){
                return Result.success(true);
            }else {
                return Result.success(false);
            }

        }catch (Exception e){

            log.info("检查版主身份失败== sectionId->{} == userId->{}",sectionId,userId);

            return Result.error("检查版主身份失败");
        }
    }

    @Override
    @Transactional
    public Result<Boolean> confirmBanUser(ConfirmBanUserDTO confirmBanUserDTO) {
        ModeratorOperationLog moderatorOperationLog = new ModeratorOperationLog();
        BannedUser bannedUser = new BannedUser();
        try {


            //插入一条用户封禁信息
            bannedUser.setBanReason(confirmBanUserDTO.getReason());
            bannedUser.setSectionId(confirmBanUserDTO.getSectionId() );
            bannedUser.setUserId(confirmBanUserDTO.getUserId());
            bannedUserMapper.insert(bannedUser);

            //插入一条日志信息 操作类型：封禁 目标类型：帖子
            moderatorOperationLog.setOperationType(OperationType.BAN);
            moderatorOperationLog.setTargetType(TargetType.USER);
            moderatorOperationLog.setReason(confirmBanUserDTO.getReason());
            moderatorOperationLog.setCreatedAt(LocalDateTime.now());
            moderatorOperationLog.setSectionId(confirmBanUserDTO.getSectionId());
            moderatorOperationLog.setTargetId(confirmBanUserDTO.getUserId());
            moderatorOperationLog.setModeratorId(BaseContext.getCurrentId());
            moderatorOperationLogMapper.insert(moderatorOperationLog);


            return Result.success(true);
        }catch (Exception e){
            return Result.error("封禁失败（数据库出错）");
        }
    }
}
