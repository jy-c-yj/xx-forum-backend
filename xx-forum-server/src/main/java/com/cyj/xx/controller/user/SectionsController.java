package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/sections")
public class SectionsController {

    @Autowired
    SectionsService sectionsService;


    @GetMapping("/hot")
    public Result<List<HotSectionsDVO>> hot(@RequestParam(defaultValue = "10") Integer limit) {

        return sectionsService.HotSection(limit);
    }

    @GetMapping("/mySection")
    public Result<List<MySectionDVO>> mySection(Long id) {


        return sectionsService.mySection(id);
    }

    @GetMapping("/sectionDetail")
    public Result<Section> sectionDetail(Long id) {

        return sectionsService.sectionDetail(id);
    }

    /**
     * 查询是否关注板块
     * @param sectionId
     * @return
     */
    @GetMapping("/isFollowSec")
    public Result<Boolean> isFollowSec(Long sectionId) {

        return sectionsService.isFollowSec(sectionId);
    }


    /**
     * 关注板块
     * @param SectionId
     * @return
     */
    @PostMapping("/followSec/{SectionId}")
    public Result<MySectionDVO> followSec(@PathVariable Long SectionId) {



        return sectionsService.followSec(SectionId);
    }

    /**
     * 取消关注
     * @param sectionId
     * @return
     */
    @DeleteMapping("/unfollowSec")
    public Result<Boolean> unfollowSec(Long sectionId) {

        return sectionsService.unfollowSec(sectionId);
    }



}
