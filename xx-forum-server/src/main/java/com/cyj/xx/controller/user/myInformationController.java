package com.cyj.xx.controller.user;



import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.*;
import com.cyj.xx.dvo.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.MyInformationServiceImpl;
import com.cyj.xx.service.Impl.SectionsServiceImpl;
import com.cyj.xx.service.Impl.UserServiceImpl;
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/user/myInformation")
@Slf4j
public class myInformationController {

    @Autowired
    MyInformationServiceImpl myInformationServiceImpl;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private SectionsServiceImpl sectionsServiceImpl;

    /**
     * 个人信息展示
     * @return
     */
    @GetMapping("")
    public Result<UserInformationDVO> myInformation() {

        return myInformationServiceImpl.UserInformation();
    }

    /**
     * 个人信息主界面板块展示
     * @return
     */
    @GetMapping("/main/sections")
    public Result<List<MyInformationMainSecsDVO>> myInformationMainSecs() {
        if(BaseContext.getCurrentId() == null){
            return Result.error("获取常逛板块失败，请检查登陆");
        }
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.getSections(userId);
    }

    /**
     * 个人信息主界面热门帖子展示
     * @return
     */
    @GetMapping("/main/posts")
    public Result<PageResult> myInformationMainPosts(int page,int pageSize) {

        return myInformationServiceImpl.getPosts(page,pageSize);
    }

    /**
     * 个人信息主界面关注板块展示
     * @return
     */
    @GetMapping("/myFollow")
    public Result<List<MyInformationFollowDVO>> myFollow() {

        if(BaseContext.getCurrentId() == null){
            return Result.error("登陆已过期");
        }
        Long userId = BaseContext.getCurrentId();

        return myInformationServiceImpl.getFollow(userId);
    }


    /**
     * 我的回复
     * @return
     */
    @GetMapping("/myReply")
    public Result<PageResult> myReply(int pageNum,int pageSize) {
        if(BaseContext.getCurrentId() == null){
            return Result.error("登陆已过期");
        }
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.getReply(userId ,pageNum,pageSize);
    }


    /**
     * 我的发帖
     * @return
     */
    @GetMapping("/myPost")
    public Result<PageResult> myPost(int pageNum,int pageSize) {
        if(BaseContext.getCurrentId() == null){
            return Result.error("登陆已过期");
        }
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.getPost(userId ,pageNum,pageSize);
    }
    /**
     * 我的收藏
     * @return
     */
    @GetMapping("/myCollection")
    public Result<PageResult> myColletion(int pageNum,int pageSize) {

        if(BaseContext.getCurrentId() == null){
            return Result.error("登陆已过期");
        }
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.getMyCollection(userId ,pageNum,pageSize);
    }

    @GetMapping("/manageSections")
    public Result<List<MySectionsDVO>> mySection() {
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.getMySections(userId);
    }
    @GetMapping("/manage/getSection")
    public Result<ManageShowSectionDVO> manageSection(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(sectionsServiceImpl.getSection(sectionId,userId)) ;
    }

    @GetMapping("/manageSections/OperationLog")
    public Result<List<OperationLogDVO>> OperationLog(Long sectionId) {
        Long userId = BaseContext.getCurrentId();
        return myInformationServiceImpl.OperationLog(userId,sectionId);
    }

}
