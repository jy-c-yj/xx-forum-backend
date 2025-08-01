package com.cyj.xx.service.Impl;

import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.dvo.SearchPost;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.admin;
import com.cyj.xx.mapper.SectionFollowsMapper;
import com.cyj.xx.mapper.SectionsMapper;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import com.cyj.xx.service.adminService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private RedisUtil redisUtil;


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
    public Result<List<MySectionDVO>> mySection(Long id) {

        if(id == null){
            return Result.error("用户数据错误");
        }

        try{
            log.info("获取缓存数据");
            String key = "Sec:followSection:UserId:"+id+":SecId:";
            Set set =  redisUtil.keys(key+"*");
            List<String> l = new ArrayList<>(set);
            List<MySectionDVO> ll = new ArrayList<>();
            if(!l.isEmpty()){
                for(String str:l){
                ll.add((MySectionDVO)redisUtil.get(str));
                }
                return Result.success(ll);
            }

            log.info("缓存数据不存在");
            List<MySectionDVO> mySec =  sectionsMapper.getMySection(id);


            log.info("未查询到缓存，执行缓存操作");
            for(MySectionDVO mySecDVO:mySec){
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
    public Result<MySectionDVO> followSec(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        String key = "Sec:followSection:UserId:"+userId+":SecId:"+sectionId;
        try {

            if (redisUtil.exists(key)) {
                return Result.error("以关注此板块");
            }

            //更新关注表
            sectionFollowsMapper.followSec(userId, sectionId);
            //更新对应板块关注数
            Section sec = sectionsMapper.getSectionById(sectionId);
            sec.setFollowersCount(sec.getFollowersCount() + 1);
            sectionsMapper.UpdateSection(sec);


            //添加缓存
            MySectionDVO mySecDVO = new MySectionDVO();
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
    public Result<PageResult> searchPost(String keyword, int page, int size) {
        try{
            PageHelper.startPage(page,size);
            Page<SearchPost> p =  sectionsMapper.searchPost(keyword);
            PageResult pageResult = new PageResult();
            pageResult.setTotal(p.getTotal());
            pageResult.setRecords(p.getResult());
            return Result.success(pageResult);

        }catch (Exception e){
            return Result.error("查询失败");
        }
    }
}
