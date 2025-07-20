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
import com.cyj.xx.entity.user_post_likes;
import com.cyj.xx.entity.user_reply_likes;
import com.cyj.xx.mapper.PostsMapper;
import com.cyj.xx.mapper.UserMapper;
import com.cyj.xx.mapper.UserPostLikesMapper;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.postsService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class PostsServiceImpl implements postsService {

    @Autowired
    PostsMapper postsMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserPostLikesMapper userPostLikesMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 分页查询热门帖子
     * @param postsPageDTO
     * @return
     */

    @Override
    public PageResult page(PostsPageDTO postsPageDTO) {
        String key = "Posts:Hot:"+postsPageDTO.getPage();
        if(redisUtil.exists(key)){
            log.info("检测到缓存"+key+"--开始取出缓存");
            List<PostsHomeDVO> ll =  redisUtil.GetAll(key);
            PageResult pageResult = PageResult.builder()
                    .records(ll)
                    .total(ll.size())
                    .build();
        }

        PageHelper.startPage(postsPageDTO.getPage(),postsPageDTO.getPageSize());
        Page<PostsHomeDVO> page =  postsMapper.page(postsPageDTO);
        PageResult pageResult = PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
        log.info("未查询到缓存，执行缓存操作");
        redisUtil.pushAll(key,page.getResult(),30, TimeUnit.SECONDS);
        return pageResult;
    }


    /**
     * 分页查询板块内帖子
     * @param secPostsPageDTO
     * @return
     */


    @Override

    public PageResult Secpage(SecPostsPageDTO secPostsPageDTO) {
        String key1 = "Posts:Sec:"+secPostsPageDTO.getSectionId()+":page:"+secPostsPageDTO.getPage();
        String key2 = "Posts:Sec:"+secPostsPageDTO.getSectionId()+":postId:";
        try{
            if(redisUtil.exists(key1)){
                log.info("数据已被缓存，开始查询 ==== 》 {}",key1);
                List<PostsHomeDVO> ll =  redisUtil.GetAll(key1);
                PageResult pageResult = PageResult.builder()
                        .records(ll)
                        .total(ll.size())
                        .build();
                return pageResult;
            }
            PageHelper.startPage(secPostsPageDTO.getPage(),secPostsPageDTO.getPageSize());
            Page<PostsHomeDVO> page =  postsMapper.Secpage(secPostsPageDTO);
            PageResult pageResult = PageResult.builder()
                    .total(page.getTotal())
                    .records(page.getResult())
                    .build();
            log.info("未查询到缓存，执行缓存操作");

            //缓存单条post
            for(PostsHomeDVO post : page.getResult()){
                Posts posts = postsMapper.getPostById(post.getId());
                String key = key2+posts.getId();
                redisUtil.set(key,posts);
            }
            //缓存分页结果
            redisUtil.pushAll(key1,page.getResult(),30, TimeUnit.SECONDS);

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

        String key = "Posts:postdetail:"+postid;
        try{
            if(redisUtil.exists(key)){
                PostDetailDVO postDetailDVO = (PostDetailDVO) redisUtil.get(key);

                if(!redisUtil.exists("likes:post:isLikes")){
                    log.info("将帖子点赞表加入缓存");
                    List<user_post_likes> ll = userPostLikesMapper.getAll();
                    for(user_post_likes like : ll){
                        String key_ = "likes:post:"+ like.getPostId() +":user:"+ like.getUserId();
                        redisUtil.set(key_,like);
                    }
                    redisUtil.set("likes:reply:isLikes","true");

                }

                return Result.success(postDetailDVO);
            }


            PostDetailDVO postDetailDVO =  postsMapper.selectPost(postid);

            log.info("添加缓存");
            redisUtil.set(key,postDetailDVO);

            return Result.success(postDetailDVO);
        }catch (Exception e){
            return Result.error("获取帖子详情出错");
        }

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



        Posts p = postsMapper.getPostById(posts.getId());
        //清空帖子分页缓存
        if(redisUtil.exists("Posts:Sec:"+p.getSectionId() +":page:*")){
            delPostPage("Posts:Sec:"+p.getSectionId() +":page:*");
        }

        //更新单个帖子缓存

        updataPost("Posts:Sec:"+ p.getSectionId() +":postId:"+p.getId(),p);
        //更新帖子详情缓存
        PostDetailDVO postDetailDVO = postsMapper.selectPost(posts.getId());
        redisUtil.set("Posts:postdetail:"+posts.getId(),postDetailDVO);


        return null;
    }


    /**
     * 点赞操作
     * @param postId
     * @return
     */

    @Override
    @Transactional
    public Result<Boolean> likePost(Long postId) {
        if(BaseContext.getCurrentId() == null){
            return Result.error("请登录");
        }
        String key_ = "Posts:postdetail:"+postId;
        Long userId = BaseContext.getCurrentId();
        String key = "likes:post:"+postId+":user:"+userId;
        try{
            if(redisUtil.exists(key)){
                return Result.error("已点赞");
            }
            //更新帖子点赞表
            userPostLikesMapper.insert(userId,postId);
            //更新帖子点赞数
            Posts posts = postsMapper.getPostById(postId);
            posts.setLikeCount(posts.getLikeCount()+1);
            postsMapper.UpdataPost(posts);
            //更新帖子发帖人获赞数
            User user = userMapper.getUserById(userId);
            user.setLikeCount(user.getLikeCount()+1);
            userMapper.updateUser(user);

            //更新帖子详情缓存
            PostDetailDVO postDetailDVO = postsMapper.selectPost(postId);
            if(redisUtil.exists(key_)){
                redisUtil.set(key_,postDetailDVO);
            }
            //更新板块内帖子分页缓存
            if(redisUtil.exists("Posts:Sec:"+posts.getSectionId()+":postId:"+postId)){
                delPostPage("Posts:Sec:"+posts.getSectionId()+":postId:"+postId);
            }
            //更新单个帖子缓存
            updataPost("Posts:Sec:"+ posts.getSectionId() +":postId:"+posts.getId(),posts);

            //更新发帖人获赞数缓存
            redisUtil.Hset("user:"+user.getId(),"likeCount",user.getLikeCount());

            //更新更新帖子点赞表缓存
            user_post_likes like = userPostLikesMapper.getlike(postId,userId);
            redisUtil.set("likes:post:"+ like.getPostId() +":user:"+ like.getUserId(),like);

            return Result.success(true);

        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }

    }


    /**
     * 取消点赞
     * @param postId
     * @return
     */
    @Override
    @Transactional
    public Result<Boolean> unlikePost(Long postId) {
        String key_ = "Posts:postdetail:"+postId;
        Long userId = BaseContext.getCurrentId();
        String key = "likes:post:"+postId+":user:"+userId;
        try{
            if(!redisUtil.exists(key)){
                return Result.error("还未点赞");
            }
            //更新帖子点赞表
            userPostLikesMapper.delete(userId,postId);
            //更新帖子点赞数
            Posts posts = postsMapper.getPostById(postId);
            posts.setLikeCount(posts.getLikeCount()-1);
            postsMapper.UpdataPost(posts);
            //更新帖子发帖人获赞数
            User user = userMapper.getUserById(userId);
            user.setLikeCount(user.getLikeCount()-1);
            userMapper.updateUser(user);

            //更新帖子详情缓存
            PostDetailDVO postDetailDVO = postsMapper.selectPost(postId);
            if(redisUtil.exists(key_)){
                redisUtil.set(key_,postDetailDVO);
            }
            //更新板块内帖子分页缓存
            if(redisUtil.exists("Posts:Sec:"+posts.getSectionId()+":postId:"+postId)){
                delPostPage("Posts:Sec:"+posts.getSectionId()+":postId:"+postId);
            }
            //更新单个帖子缓存
            updataPost("Posts:Sec:"+ posts.getSectionId() +":postId:"+posts.getId(),posts);

            //更新发帖人获赞数缓存
            redisUtil.Hset("user:"+user.getId(),"likeCount",user.getLikeCount());

            //更新更新帖子点赞表缓存
            //删除缓存
            if(redisUtil.exists(key)){
                redisUtil.delete(key);
            }

            return Result.success(true);

        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }
    }


    /**
     * 检查点赞状态
     * @param postId
     * @return
     */
    @Override
    public Result<Boolean> checkLike(Long postId) {
        if(BaseContext.getCurrentId()==null){
            return Result.success(false);
        }
        Long userId = BaseContext.getCurrentId();
        String key = "likes:post:"+postId+":user:"+userId;

        try{

            log.info("在缓存中检查");
            if(redisUtil.exists(key)){
                //存在
                return Result.success(true);
            }
            //不存在
            return Result.success(false);
        }catch (Exception e){
            return Result.error("检查点赞状态出错误");
        }
    }

    //更新单条缓存
    public void updataPost(String key,Posts post){

        redisUtil.set(key,post);
    }

    //清空分页缓存
    public void delPostPage(String pattern){
        Set<String> kk = redisUtil.keys(pattern);
        redisUtil.delete(kk.toArray(new String[0]));
    }

}
