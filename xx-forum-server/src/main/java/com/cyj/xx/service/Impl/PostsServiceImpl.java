package com.cyj.xx.service.Impl;

import com.cyj.xx.Enum.OperationType;
import com.cyj.xx.Enum.TargetType;
import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.ConfirmBanPostDTO;
import com.cyj.xx.dto.PostPushDTO;
import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.SecPostsPageDTO;
import com.cyj.xx.dvo.*;
import com.cyj.xx.entity.*;
import com.cyj.xx.exception.UserBannedException;
import com.cyj.xx.mapper.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.postsService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    UserPostCollectionMapper userPostCollectionMapper;

    @Autowired
    SectionsMapper sectionsMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    ModeratorOperationLogMapper moderatorOperationLogMapper;

    @Autowired
    BannedPostMapper bannedPostMapper;
    @Autowired
    private BannedUserMapper bannedUserMapper;

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
        String key1_t = "Posts:Sec:"+secPostsPageDTO.getSectionId()+":page_t:"+secPostsPageDTO.getPage();//时间排序
        String key1_v = "Posts:Sec:"+secPostsPageDTO.getSectionId()+":page_v:"+secPostsPageDTO.getPage();//热度/观看数排序
        String key2 = "Posts:Sec:"+secPostsPageDTO.getSectionId()+":postId:";
        try{
            if(secPostsPageDTO.getSortType() == 1){
                if(redisUtil.exists(key1_t)){
                    log.info("数据已被缓存，开始查询 ==== 》 {}",key1_t);
                    List<PostsSectionDVO> ll =  redisUtil.GetAll(key1_t);
                    PageResult pageResult = PageResult.builder()
                            .records(ll)
                            .total(ll.size())
                            .build();
                    return pageResult;
                }
            }
            if(secPostsPageDTO.getSortType() == 2){
                if(redisUtil.exists(key1_v)){
                    log.info("数据已被缓存，开始查询 ==== 》 {}",key1_v);
                    List<PostsSectionDVO> ll =  redisUtil.GetAll(key1_v);
                    PageResult pageResult = PageResult.builder()
                            .records(ll)
                            .total(ll.size())
                            .build();
                    return pageResult;
                }
            }


            PageResult pageResult = new PageResult();
            //按时间排序
            if (secPostsPageDTO.getSortType() == 1){
                PageHelper.startPage(secPostsPageDTO.getPage(),secPostsPageDTO.getPageSize());
                Page<PostsSectionDVO> page =  postsMapper.Secpage_t(secPostsPageDTO);
                pageResult.setRecords(page.getResult());
                pageResult.setTotal(page.getTotal());
                log.info("未查询到缓存，执行缓存操作");

                //缓存单条post
                for(PostsSectionDVO post : page.getResult()){
                    Posts posts = postsMapper.getPostById(post.getId());
                    String key = key2+posts.getId();
                    redisUtil.set(key,posts);
                }
                //缓存分页结果
                redisUtil.pushAll(key1_t,page.getResult(),30, TimeUnit.SECONDS);


            }
            //按热度排序
            if(secPostsPageDTO.getSortType() == 2){
                PageHelper.startPage(secPostsPageDTO.getPage(),secPostsPageDTO.getPageSize());
                Page<PostsSectionDVO> page =  postsMapper.Secpage_v(secPostsPageDTO);
                pageResult.setRecords(page.getResult());
                pageResult.setTotal(page.getTotal());
                log.info("未查询到缓存，执行缓存操作");

                //缓存单条post
                for(PostsSectionDVO post : page.getResult()){
                    Posts posts = postsMapper.getPostById(post.getId());
                    String key = key2+posts.getId();
                    redisUtil.set(key,posts);
                }
                //缓存分页结果
                redisUtil.pushAll(key1_v,page.getResult(),30, TimeUnit.SECONDS);


            }
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
    public Result<PostDetailDVO> postdetail(Long postid) {

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
                    redisUtil.set("likes:post:isLikes","true");

                }
                if(!redisUtil.exists("collection:post:isCollect")){
                    log.info("将帖子收藏表加入缓存");
                    List<user_post_collection> ll = userPostCollectionMapper.getAll();
                    for(user_post_collection col : ll){
                        String key_ = "likes:post:"+ col.getPostId() +":user:"+ col.getUserId();
                        redisUtil.set(key_,col);
                    }
                    redisUtil.set("collection:post:isCollect","true");

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
        if(viewCount <= 0){
            Posts pp = postsMapper.getPostById(id);
            viewCount = pp.getViewCount();
        }

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
            //更新帖子点赞表
            userPostLikesMapper.insert(userId,postId);
            //更新帖子点赞数
            Posts posts = postsMapper.getPostById(postId);
            posts.setLikeCount(posts.getLikeCount()+1);
            postsMapper.UpdataPost(posts);
            //更新帖子发帖人获赞数
            User user = userMapper.getUserById(posts.getUserId());
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

    /**
     * 收藏操作
     * @param postId
     * @return
     */

    @Override
    public Result<Boolean> collectPost(Long postId) {
        if(BaseContext.getCurrentId() == null){
            return Result.error("请登录");
        }
        String key_ = "Posts:postdetail:"+postId;
        Long userId = BaseContext.getCurrentId();
        String key = "collection:post:"+postId+":user:"+userId;
        try{
            if(redisUtil.exists(key)){
                return Result.error("已收藏");
            }
            //更新帖子收藏表
            userPostCollectionMapper.insert(userId,postId);
            //更新帖子收藏数
            Posts posts = postsMapper.getPostById(postId);
            posts.setLikeCount(posts.getCollectCount()+1);
            postsMapper.UpdataPost(posts);

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

            //更新更新帖子收藏表缓存
            user_post_collection collection = userPostCollectionMapper.getlike(postId,userId);
            redisUtil.set("collection:post:"+postId+":user:"+userId,collection);

            return Result.success(true);

        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }

    }

    /**
     * 取消收藏
     * @param postId
     * @return
     */
    @Override
    public Result<Boolean> uncollectPost(Long postId) {
        String key_ = "Posts:postdetail:"+postId;
        Long userId = BaseContext.getCurrentId();
        String key = "collection:post:"+postId+":user:"+userId;
        try{
            if(!redisUtil.exists(key)){
                return Result.error("还未点赞");
            }
            //更新帖子收藏表
            userPostCollectionMapper.delete(userId,postId);
            //更新帖子收藏数
            Posts posts = postsMapper.getPostById(postId);
            posts.setLikeCount(posts.getCollectCount()-1);
            postsMapper.UpdataPost(posts);

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

            //更新更新帖子收藏表缓存
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
     * 检查收藏状态
     * @param postId
     * @return
     */
    @Override
    public Result<Boolean> checkCollect(Long postId) {
        if(BaseContext.getCurrentId()==null){
            return Result.success(false);
        }
        Long userId = BaseContext.getCurrentId();
        String key = "collection:post:"+postId+":user:"+userId;

        try{

            log.info("在缓存中检查");
            if(redisUtil.exists(key)){
                //存在
                return Result.success(true);
            }
            //不存在
            return Result.success(false);
        }catch (Exception e){
            return Result.error("检查post收藏状态出错误");
        }
    }


    /**
     * 发帖
     * @param postPushDTO
     * @return
     */
    @Override
    @Transactional
    public Result<Posts> pushPost(PostPushDTO postPushDTO) {


        if(BaseContext.getCurrentId() == null){
            return Result.error("登陆已过期");
        }
        BannedUser bannedUser = bannedUserMapper.isBanned(BaseContext.getCurrentId(),Long.parseLong(postPushDTO.getSectionId().toString()));
        if(bannedUser != null){
            throw new UserBannedException();
        }


        try{
            //

            Posts posts = Posts.builder()
                    .title(postPushDTO.getTitle())
                    .content(postPushDTO.getContent())
                    .sectionId(postPushDTO.getSectionId())
                    .userId(BaseContext.getCurrentId())
                    .likeCount(0)
                    .collectCount(0)
                    .createdTime(LocalDateTime.now())
                    .updatedTime(LocalDateTime.now())
                    .isEssence(false)
                    .isTop(false)
                    .viewCount(0)
                    .status(0)
                    .replyCount(0)
                    .build();

            postsMapper.addNewPost(posts);

            //更新板块表帖子数
            Section sec = sectionsMapper.getSectionById(Long.parseLong(Integer.toString(postPushDTO.getSectionId())) );
            sec.setPostCount(sec.getPostCount()+1);
            sectionsMapper.UpdateSection(sec);
            //更新用户发帖数
            User user = userMapper.getUserById(BaseContext.getCurrentId());
            user.setPostCount(user.getPostCount()+1);
            userMapper.updateUser(user);

            //更新板块帖子详情缓存
            String key1 ="Posts:Sec:"+ postPushDTO.getSectionId()+":postId:"+posts.getId();
            if(redisUtil.exists(key1)){
                redisUtil.set(key1,posts);
            }
            //更新板块帖子详情缓存
            String key2 ="Sec:sectionDetail:SectionId:"+sec.getId();
            if(redisUtil.exists(key2)){
                redisUtil.set(key2,sec);
            }
            //删除板块帖子分页显示缓存
            delPostPage("Posts:Sec:"+ postPushDTO .getSectionId()+":page:*");

            //更新用户缓存
            if(redisUtil.exists("user:"+user.getId())){
                redisUtil.Hset("user:"+user.getId(),"postCount",user.getPostCount());
            }

            return Result.success(posts);

        }catch (Exception e){
            return Result.error("发帖失败");
        }

    }

    //更新单条缓存
    public void updataPost(String key,Posts post){

        redisUtil.set(key,post);
    }

    //清空分页缓存
    public void delPostPage(String pattern){
        Set<String> kk = redisUtil.keys(pattern);
        if(kk.isEmpty()){
            return;
        }
        redisUtil.delete(kk.toArray(new String[0]));
    }


    @Override
    public Result<PageResult> searchPost(String keyword, int page, int size) {
        try{
            PageHelper.startPage(page,size);
            Page<SearchPostDVO> p =  postsMapper.searchPost(keyword);
            PageResult pageResult = new PageResult();
            pageResult.setTotal(p.getTotal());
            pageResult.setRecords(p.getResult());
            return Result.success(pageResult);

        }catch (Exception e){
            return Result.error("查询失败");
        }
    }

    @Override
    public Result<PageResult> searchPostInsec(String keyword, int page, int size ,int sectionId) {
        try{
            PageHelper.startPage(page,size);
            Page<SearchPostDVO> p =  postsMapper.searchPostInSec(keyword,sectionId);
            PageResult pageResult = new PageResult();
            pageResult.setTotal(p.getTotal());
            pageResult.setRecords(p.getResult());
            return Result.success(pageResult);

        }catch (Exception e){
            return Result.error("查询失败");
        }
    }

    @Override
    @Transactional
    public Result<Boolean> confirmBanPost(ConfirmBanPostDTO confirmBanPostDTO) {
        ModeratorOperationLog moderatorOperationLog = new ModeratorOperationLog();
        BannedPost bannedPost = new BannedPost();
        try {

            //修改post封禁状态(0=正常,1=删除)
            Posts posts = postsMapper.getPostById(confirmBanPostDTO.getPostId());
            posts.setStatus(1);
            postsMapper.UpdataPost(posts);

            //插入一条帖子封禁信息
            bannedPost.setBanReason(confirmBanPostDTO.getReason());
            bannedPost.setSectionId(Long.parseLong(posts.getSectionId().toString()) );
            bannedPost.setPostId(confirmBanPostDTO.getPostId());
            bannedPostMapper.insert(bannedPost);

            //插入一条日志信息 操作类型：封禁 目标类型：帖子
            moderatorOperationLog.setOperationType(OperationType.BAN);
            moderatorOperationLog.setTargetType(TargetType.POST);
            moderatorOperationLog.setReason(confirmBanPostDTO.getReason());
            moderatorOperationLog.setCreatedAt(LocalDateTime.now());
            moderatorOperationLog.setSectionId(Long.parseLong(posts.getSectionId().toString()));
            moderatorOperationLog.setTargetId(confirmBanPostDTO.getPostId());
            moderatorOperationLog.setModeratorId(BaseContext.getCurrentId());
            moderatorOperationLogMapper.insert(moderatorOperationLog);


            return Result.success(true);
        }catch (Exception e){
            return Result.error("封禁失败（数据库出错）");
        }
    }


    /**
     * 帖子置顶
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public Result<Boolean> pinPost(Long userId, Long postId) {
        log.info("/**********************************************/\n" +
                "》》帖子置顶《《\n" +
                "操作用户:{}\n目标帖子:{}\n" +
                "/**********************************************/");
        ModeratorOperationLog moderatorOperationLog = new ModeratorOperationLog();
        try {
            Posts posts = postsMapper.getPostById(postId);
            //修改帖子状态
            posts.setIsTop(true);
            postsMapper.UpdataPost(posts);
            //记录操作日志
            moderatorOperationLog.setOperationType(OperationType.PIN);
            moderatorOperationLog.setTargetType(TargetType.POST);
            moderatorOperationLog.setModeratorId(userId);
            moderatorOperationLog.setTargetId(postId);
            moderatorOperationLog.setSectionId(Long.parseLong(posts.getSectionId().toString()) );
            moderatorOperationLog.setReason("置顶操作");
            moderatorOperationLogMapper.insert(moderatorOperationLog);
            return Result.success(true);
        }catch (Exception e){
            log.error("帖子置顶失败");
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消置顶
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public Result<Boolean> unpinPost(Long userId, Long postId) {
        log.info("/**********************************************/\n" +
                "》》取消置顶《《\n" +
                "操作用户:{}\n目标帖子:{}\n" +
                "/**********************************************/");
        ModeratorOperationLog moderatorOperationLog = new ModeratorOperationLog();
        try {
            Posts posts = postsMapper.getPostById(postId);
            //修改帖子状态
            posts.setIsTop(false);
            postsMapper.UpdataPost(posts);
            //记录操作日志
            moderatorOperationLog.setOperationType(OperationType.UNPIN);
            moderatorOperationLog.setTargetType(TargetType.POST);
            moderatorOperationLog.setModeratorId(userId);
            moderatorOperationLog.setTargetId(postId);
            moderatorOperationLog.setSectionId(Long.parseLong(posts.getSectionId().toString()) );
            moderatorOperationLog.setReason("取消操作");
            moderatorOperationLogMapper.insert(moderatorOperationLog);
            return Result.success(true);
        }catch (Exception e){
            log.error("取消置顶失败");
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<List<BanPostLogDVO>> getBanPostLog(Long sectionId) {
        try{

            List<BanPostLogDVO> ll = bannedPostMapper.selectBanPostLog(sectionId);
            return Result.success(ll);

        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> unBanPost(Long id) {
        try{

            BannedPost bannedPost= bannedPostMapper.getById(id);
            bannedPostMapper.deleteById(id);

            //记录操作日志
            ModeratorOperationLog moderatorOperationLog = new ModeratorOperationLog();
            moderatorOperationLog.setOperationType(OperationType.UNBAN);
            moderatorOperationLog.setTargetType(TargetType.POST);
            moderatorOperationLog.setReason(bannedPost.getBanReason());
            moderatorOperationLog.setCreatedAt(LocalDateTime.now());
            moderatorOperationLog.setSectionId(bannedPost.getSectionId());
            moderatorOperationLog.setTargetId(bannedPost.getPostId());
            moderatorOperationLog.setModeratorId(BaseContext.getCurrentId());
            moderatorOperationLogMapper.insert(moderatorOperationLog);
            return Result.success();

        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}
