package com.cyj.xx.service.Impl;

import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.admin;
import com.cyj.xx.mapper.SectionsMapper;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import com.cyj.xx.service.adminService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SectionsServiceImpl implements SectionsService {

    @Autowired
    private SectionsMapper sectionsMapper;


    /**
     * 热门板块显示
     * @param limit
     * @return
     */

    @Override
    public Result<List<HotSectionsDVO>> HotSection(Integer limit) {
        try{
            PageHelper.startPage(1, 10);
            List<HotSectionsDVO> hotSec = sectionsMapper.PageHotSec();


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

            List<MySectionDVO> mySec =  sectionsMapper.getMySection(id);

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
