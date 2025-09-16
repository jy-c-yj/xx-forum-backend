package com.cyj.xx.service.Impl;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dvo.*;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.User;
import com.cyj.xx.entity.UserSignIn;
import com.cyj.xx.mapper.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.myInformationService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class MyInformationServiceImpl implements myInformationService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    SectionsMapper sectionsMapper;

    @Autowired
    PostsMapper postsMapper;

    @Autowired
    SectionFollowsMapper sectionFollowsMapper;

    @Autowired
    UserSignInMapper userSignInMapper;

    @Autowired
    RepliesMapper repliesMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ModeratorOperationLogMapper moderatorOperationLogMapper;




    /**
     * 个人信息展示页面
     * @param
     * @return
     */
    @Override
    public Result<UserInformationDVO> UserInformation() {

        if(BaseContext.getCurrentId() == null) {
            return Result.error("未登录");
        }
        Long id = BaseContext.getCurrentId();

        UserInformationDVO userInformationDVO = new UserInformationDVO();

        try{
            String key = "user:"+id;
            if(redisUtil.exists(key)){
                Map<String,Object> user = redisUtil.hgetAll(key);
                userInformationDVO.setUsername(user.get("username").toString());
                userInformationDVO.setId(id);
                userInformationDVO.setLikeCount((Integer) user.get("likeCount"));
                userInformationDVO.setPostCount((Integer) user.get("postCount"));
                userInformationDVO.setImage(user.get("file").toString());

                return Result.success(userInformationDVO);
            }

            User user = userMapper.getUserById(id);
            userInformationDVO.setUsername(user.getUsername());
            userInformationDVO.setId(id);
            userInformationDVO.setLikeCount(user.getLikeCount());
            userInformationDVO.setPostCount(user.getPostCount());
            userInformationDVO.setImage(user.getImage());

            return Result.success(userInformationDVO);

        }catch (Exception e) {
            return Result.error("查询user信息出错");
        }
    }

    /**
     * 个人信息主界面板块展示
     * @return
     */
    @Override
    public Result<List<MyInformationMainSecsDVO>> getSections(Long userId) {
        try{
            List<MyFollowSectionDVO> ll = sectionsMapper.getFollowMySection(userId);
            List<MyInformationMainSecsDVO> lll = new ArrayList<>();
            for(MyFollowSectionDVO s : ll){
                MyInformationMainSecsDVO mps = new MyInformationMainSecsDVO();
                mps.setId(s.getId());
                mps.setSectionName(s.getName());
                lll.add(mps);
            }
            return Result.success(lll);

        }catch (Exception e) {
            return Result.error("获取常逛板块失败");
        }

    }


    /**
     * 个人信息主界面热门帖子展示
     * @return
     */
    @Override
    public Result<PageResult> getPosts(int page ,int pageSize) {
        if(BaseContext.getCurrentId() == null){
            return Result.error("获取热门动态失败，请检查登陆");
        }
        Long userId = BaseContext.getCurrentId();
        try{
            PageHelper.startPage(page, pageSize);
            Page<MyInformationMainPostsDVO> p =  postsMapper.getPostByUserId(userId);
            PageResult pr = new PageResult();
            pr.setTotal(p.getTotal());
            pr.setRecords(p.getResult());
            return Result.success(pr);


        }catch (Exception e) {
            return Result.error("获取热门动态失败");
        }
    }

    /**
     * 个人信息主界面关注板块展示
     * @param userId
     * @return
     */

    @Override
    public Result<List<MyInformationFollowDVO>> getFollow(Long userId) {
        String key = "Sec:followSection:UserId:"+userId+":SecId:";

        try{
            Set<String> s = redisUtil.keys(key+"*");
            List<MyInformationFollowDVO> ll = new ArrayList<>();
//            if(!s.isEmpty()){
//                for(String str : s){
//                   MySectionDVO my =  (MySectionDVO) redisUtil.get(str);
//                   MyInformationFollowDVO mps = new MyInformationFollowDVO();
//                   mps.setId(my.getId());
//                   mps.setSectionName(my.getName());
//                   ll.add(mps);
//                }
//            }
//            //查找数据库
//            List<sections_follows> ls = sectionFollowsMapper.findMySections(userId);
//            for(sections_follows sf : ls){
//                Section sec = sectionsMapper.getSectionById(sf.getSectionId());
//                MyInformationFollowDVO mps = new MyInformationFollowDVO();
//                mps.setId(sec.getId());
//                mps.setSectionName(sec.getName());
//            }
            //查询签到表
            List<UserSignIn> ls = userSignInMapper.getUserSignInByUserId(userId);
            for(UserSignIn u : ls){
                MyInformationFollowDVO mps = new MyInformationFollowDVO();
                mps.setId(u.getSectionId());
                Section sec = sectionsMapper.getSectionById(u.getSectionId());
                mps.setSectionName(sec.getName());
                mps.setXp(u.getExperience());
                ll.add(mps);

            }
            return Result.success(ll);


        }catch (Exception e) {
            return Result.error("关注板块获取失败");
        }

    }

    /**
     * 我的回复
     * @param userId
     * @return
     */
    @Override
    public Result<PageResult> getReply(Long userId,int pageNum,int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            Page<MyInformationReplyDVO> page = repliesMapper.getMyRepliesById(userId);
            PageResult pr = new PageResult();
            pr.setTotal(page.getTotal());
            pr.setRecords(page.getResult());
            return Result.success(pr);


        } catch (Exception e) {
            return Result.error("回复获取失败");
        }
    }


    /**
     * 我的发帖
     * @return
     */
    @Override
    public Result<PageResult> getPost(Long userId, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            Page<MyInformationPostDVO> page = postsMapper.getMyPostsByUserId(userId);
            PageResult pr = new PageResult();
            pr.setTotal(page.getTotal());
            pr.setRecords(page.getResult());
            return Result.success(pr);


        } catch (Exception e) {
            log.error("获取用户帖子失败 - 用户ID: {}, 页码: {}, 页大小: {}",
                    userId, pageNum, pageSize, e);
            return Result.error("获取帖子失败: " + e.getMessage());
        }
    }



    /**
     * 我的收藏
     * @return
     */
    @Override
    public Result<PageResult> getMyCollection(Long userId, int pageNum, int pageSize) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            Page<MyInformationMyCollectionDVO> page = postsMapper.getMyCollectionByUserId(userId);
            PageResult pr = new PageResult();
            pr.setTotal(page.getTotal());
            pr.setRecords(page.getResult());
            return Result.success(pr);


        } catch (Exception e) {
            log.error("获取用户帖子失败");
            return Result.error("获取帖子失败: " + e.getMessage());
        }
    }

    /**
     * 获取我创造的板块
     * @param userId
     * @return
     */
    @Override
    public Result<List<MySectionsDVO>> getMySections(Long userId) {

        List<MySectionsDVO> ldvo = new ArrayList<>();

        try{

            List<Section> lsec =  sectionsMapper.getSection(userId);

            for(Section sec : lsec){
                MySectionsDVO mps = new MySectionsDVO();
                mps.setId(sec.getId());
                mps.setSectionName(sec.getName());
                ldvo.add(mps);
            }


            return Result.success(ldvo);

        }catch (Exception e) {
            return Result.error("查询数据库失败");
        }
    }


    /**
     * 查询操作日志
     * @param userId
     * @param sectionId
     * @return
     */
    @Override
    public Result<List<OperationLogDVO>> OperationLog(Long userId, Long sectionId) {
        log.info("开始查询日志（userId: {}, sectionId: {}）", userId, sectionId);
        List<OperationLogDVO> ldvo = new ArrayList<>();
        try{
            ldvo = moderatorOperationLogMapper.SelectBySectionId(sectionId);

            return Result.success(ldvo);
        }catch (Exception e) {
            log.error("查询日志失败（userId: {}, sectionId: {}）", userId, sectionId);
            return Result.error("查询数据库失败");
        }
    }
}
