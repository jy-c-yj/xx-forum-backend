package com.cyj.xx.service.Impl;

import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.SecPostsPageDTO;
import com.cyj.xx.dto.UpdataPostLikeDTO;
import com.cyj.xx.dvo.PostDetailDVO;
import com.cyj.xx.dvo.PostsHomeDVO;
import com.cyj.xx.dvo.UpdataPostLikedDVO;
import com.cyj.xx.entity.Posts;
import com.cyj.xx.entity.User;
import com.cyj.xx.mapper.PostsMapper;
import com.cyj.xx.mapper.UserMapper;
import com.cyj.xx.mapper.UserPostLikesMapper;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.postsService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class PostsServiceImpl implements postsService {

    @Autowired
    PostsMapper postsMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserPostLikesMapper userPostLikesMapper;

    /**
     * 分页查询热门帖子
     * @param postsPageDTO
     * @return
     */

    @Override
    public PageResult page(PostsPageDTO postsPageDTO) {

        PageHelper.startPage(postsPageDTO.getPage(),postsPageDTO.getPageSize());
        Page<PostsHomeDVO> page =  postsMapper.page(postsPageDTO);
        PageResult pageResult = PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
        return pageResult;
    }


    /**
     * 分页查询板块内帖子
     * @param secPostsPageDTO
     * @return
     */


    @Override
    public PageResult Secpage(SecPostsPageDTO secPostsPageDTO) {
        try{
            PageHelper.startPage(secPostsPageDTO.getPage(),secPostsPageDTO.getPageSize());
            Page<PostsHomeDVO> page =  postsMapper.Secpage(secPostsPageDTO);
            PageResult pageResult = PageResult.builder()
                    .total(page.getTotal())
                    .records(page.getResult())
                    .build();
            return pageResult;
        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }

    }

    /**
     * 显示帖子的详细内容
     * @param postid
     * @return
     */
    @Override
    public Result postdetail(Long postid) {
        Long id = Long.parseLong("-1");

        if(BaseContext.getCurrentId() == null){
            BaseContext.setCurrentId(id);
        }

        PostDetailDVO postDetailDVO =  postsMapper.selectPost(postid, BaseContext.getCurrentId());
        log.info("postDetailDVO==>>{}",postDetailDVO.toString());
//        log.info("观看数加一");
//        Posts posts = new Posts();
//        posts.setId(postid);
//        posts.setViewCount(postDetailDVO.getViewCount()+1);
//        postsMapper.UpdataPost(posts);

        return Result.success(postDetailDVO);
    }


    /**
     * 增加帖子浏览量
     * @param id
     * @return
     */
    @Override
    public Result addViewCount(Long id , Integer viewCount) {

        if(id == null){
            return Result.error("操作错误");
        }
        Posts posts = new Posts();
        posts.setId(id);
        log.info("观看数增加  viewCount{} =======> {}",viewCount,viewCount+1);
        viewCount++;
        posts.setViewCount(viewCount);
        postsMapper.UpdataPost(posts);


        return null;
    }

    @Override
    @Transactional
    public Result addlikeCount(UpdataPostLikeDTO updataPostLikeDTO) {

        if(updataPostLikeDTO.getPostId()  == null || updataPostLikeDTO.getUsername() == null) {
            return Result.error("操作错误");
        }
        log.info("开始执行点赞操作updataPostLikeDTO======》》》{}",updataPostLikeDTO);

        try {
            User user = userMapper.getUserByUserName(updataPostLikeDTO.getUsername());
            Posts post = Posts.builder().id(updataPostLikeDTO.getPostId()).likeCount(updataPostLikeDTO.getLikeCount()).build();
            UpdataPostLikedDVO updataPostLikedDVO = new UpdataPostLikedDVO();
            if(updataPostLikeDTO.isLiked()){
                log.info("执行取消点赞操作updataPostLikeDTO.getLikeCount()====>{}",updataPostLikeDTO.getLikeCount());

                updataPostLikeDTO.setLikeCount(updataPostLikeDTO.getLikeCount()-1);

                log.info("取消点赞操作执行完毕post.getLikeCount()====>{}",updataPostLikeDTO.getLikeCount());

                user.setLikeCount(user.getLikeCount()-1);

                userPostLikesMapper.delete(BaseContext.getCurrentId(),updataPostLikeDTO.getPostId());

                log.info("取消点赞操作执行完毕user.getLikeCount()====>{}",user.getLikeCount());


            }else {
                log.info("执行点赞操作updataPostLikeDTO.getLikeCount()====>{}",updataPostLikeDTO.getLikeCount());

                updataPostLikeDTO.setLikeCount(updataPostLikeDTO.getLikeCount()+1);

                log.info("点赞操作执行完毕post.getLikeCount()====>{}",updataPostLikeDTO.getLikeCount());

                user.setLikeCount(user.getLikeCount()+1);

                userPostLikesMapper.insert(BaseContext.getCurrentId(),updataPostLikeDTO.getPostId());

                log.info("点赞操作执行完毕user.getLikeCount()====>{}",user.getLikeCount());
                updataPostLikedDVO.setLiked(true);

            }

            post.setLikeCount(updataPostLikeDTO.getLikeCount());
            userMapper.updateUser(user);
            postsMapper.UpdataPost(post);

            updataPostLikedDVO.setLikeCount(updataPostLikeDTO.getLikeCount());

            log.info("updataPostLikedDVO=======>{}",updataPostLikedDVO);


            return Result.success(updataPostLikedDVO);
        }catch (Exception e){
            e.printStackTrace();
        }



        return Result.error("操作失败");
    }

}
