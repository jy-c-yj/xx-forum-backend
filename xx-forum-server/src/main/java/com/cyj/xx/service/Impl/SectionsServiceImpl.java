package com.cyj.xx.service.Impl;

import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.admin;
import com.cyj.xx.mapper.SectionsMapper;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import com.cyj.xx.service.adminService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SectionsServiceImpl implements SectionsService {

    @Autowired
    private SectionsMapper sectionsMapper;

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
            String key = "Sec:my:"+id;
            if(redisUtil.exists(key)){
                log.info("获取缓存数据");
                List<MySectionDVO> ll = redisUtil.GetAll(key);
                return Result.success(ll);

            }

            List<MySectionDVO> mySec =  sectionsMapper.getMySection(id);
            log.info("未查询到缓存，执行缓存操作");
            redisUtil.pushAll(key,mySec,20, TimeUnit.HOURS);

            return Result.success(mySec);

        }catch (Exception e){
            throw new RuntimeException("数据查询错误");
        }


    }


    @Override
    public Result<Section> sectionDetail(Long id) {

        try{
            log.info("开始查询板块 当前板块id ===> {}",id);
            Section sec =  sectionsMapper.getSectionById(id);
            return Result.success(sec);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }


    }
}
