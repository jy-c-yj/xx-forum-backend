package com.cyj.xx.service.Impl;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.*;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.User;
import com.cyj.xx.entity.sections_follows;
import com.cyj.xx.exception.PasswordErrorException;
import com.cyj.xx.mapper.*;
import com.cyj.xx.properties.JwtProperties;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.myInformationService;
import com.cyj.xx.service.userService;
import com.cyj.xx.utils.AliOssUtil;
import com.cyj.xx.utils.JwtUtil;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    RepliesMapper repliesMapper;

    @Autowired
    RedisUtil redisUtil;




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
            List<MySectionDVO> ll = sectionsMapper.getMySection(userId);
            List<MyInformationMainSecsDVO> lll = new ArrayList<>();
            for(MySectionDVO s : ll){
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
            if(!s.isEmpty()){
                for(String str : s){
                   MySectionDVO my =  (MySectionDVO) redisUtil.get(str);
                   MyInformationFollowDVO mps = new MyInformationFollowDVO();
                   mps.setId(my.getId());
                   mps.setSectionName(my.getName());
                   ll.add(mps);
                }
            }
            //查找数据库
            List<sections_follows> ls = sectionFollowsMapper.findMySections(userId);
            for(sections_follows sf : ls){
                Section sec = sectionsMapper.getSectionById(sf.getSectionId());
                MyInformationFollowDVO mps = new MyInformationFollowDVO();
                mps.setId(sec.getId());
                mps.setSectionName(sec.getName());
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
}
