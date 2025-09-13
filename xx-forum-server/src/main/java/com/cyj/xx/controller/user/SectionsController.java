package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.ConfirmBanPostDTO;
import com.cyj.xx.dto.ConfirmBanUserDTO;
import com.cyj.xx.dto.CreateSectionDTO;
import com.cyj.xx.dto.UpdataSectionDTO;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MyFollowSectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.SectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/sections")
@Slf4j
public class SectionsController {

    @Autowired
    SectionsService sectionsService;


    @GetMapping("/hot")
    public Result<List<HotSectionsDVO>> hot(@RequestParam(defaultValue = "10") Integer limit) {

        return sectionsService.HotSection(limit);
    }

    @GetMapping("/mySection")
    public Result<List<MyFollowSectionDVO>> mySection(Long id) {


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
    public Result<MyFollowSectionDVO> followSec(@PathVariable Long SectionId) {



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


    /**
     * 搜索相关板块
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/section/search")
    public Result<PageResult> searchPost(@RequestParam("q") String keyword,
                                         @RequestParam(value = "pageNum", defaultValue = "0") int page,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int size){

        return sectionsService.searchSection(keyword,page,size);

    }

    @PostMapping("/createSection")
    public Result<Boolean> createSection(@RequestBody CreateSectionDTO createSectionDTO) {
        Long userId = BaseContext.getCurrentId();
        if(userId == null){
            return Result.error("请登录");
        }
        return sectionsService.createSection(userId,createSectionDTO);
    }

    @PostMapping("/UpdataSection")
    public Result<Boolean> updataSection(@RequestBody UpdataSectionDTO updataSectionDTO) {
        Long userId = BaseContext.getCurrentId();
        return sectionsService.UpdataSection(updataSectionDTO);
    }

    @GetMapping("/isModerator")
    public Result<Boolean> isModerator(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        return sectionsService.isModerator(sectionId,userId);
    }

    @PostMapping("/confirmBanUser")
    public Result<Boolean> confirmBanUser(@RequestBody ConfirmBanUserDTO confirmBanUserDTO) {

        return sectionsService.confirmBanUser(confirmBanUserDTO);
    }



}
