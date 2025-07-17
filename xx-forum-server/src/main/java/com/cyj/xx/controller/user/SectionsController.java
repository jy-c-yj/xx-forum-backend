package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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



}
