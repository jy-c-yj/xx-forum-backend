package com.cyj.xx.service.Impl;

import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.dvo.ReplyDVO;
import com.cyj.xx.entity.*;
import com.cyj.xx.mapper.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.repliesService;
import com.cyj.xx.utils.RedisUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class RepliesServiceImpl implements repliesService {

    @Autowired
    RepliesMapper repliesMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserReplyLikesMapper userReplyLikesMapper;
    @Autowired
    PostsMapper postsMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private InteractionNotificationsMapper interactionNotificationsMapper;

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    @Override
    public PageResult getReply(RepliesPageDTO repliesPageDTO) {
        Long id = Long.parseLong("-1");
        int type = repliesPageDTO.getType();
        String key1 = "replies:PostId:"+repliesPageDTO.getPostid()+":page:hot:"+repliesPageDTO.getPage();
        String key1_2 = "replies:PostId:"+repliesPageDTO.getPostid()+":page:Proper:"+repliesPageDTO.getPage();
        String key1_3 = "replies:PostId:"+repliesPageDTO.getPostid()+":page:Reverse:"+repliesPageDTO.getPage();
        String key2 = "replies:PostId:"+repliesPageDTO.getPostid()+":replyId:";
        try{
            //如果缓存存在
            if(type == 1){//热门查询
                if(redisUtil.exists(key1)){
                    log.info("查询到分页缓存");
                    List<ReplyDVO> replies = redisUtil.GetAll(key1);
                    PageResult pageResult = PageResult.builder()
                            .total(replies.size())
                            .records(replies)
                            .build();
                    log.info("检查点赞数据是否以被缓存");
                    if(!redisUtil.exists("likes:isLikes")){
                        log.info("不存在，将点赞表缓存");
                        List<user_reply_likes> ll = userReplyLikesMapper.getAll();
                        for(user_reply_likes like : ll){
                            String key_ = "likes:reply:"+ like.getReplyId() +":user:"+ like.getUserId();
                            redisUtil.set(key_,like);
                        }
                        redisUtil.set("likes:reply:isLikes","true");
                    }
                    return pageResult;
                }
            }
            if(type == 2){//正序查询
                if(redisUtil.exists(key1_2)){
                    log.info("查询到分页缓存");
                    List<ReplyDVO> replies = redisUtil.GetAll(key1_2);
                    PageResult pageResult = PageResult.builder()
                            .total(replies.size())
                            .records(replies)
                            .build();
                    log.info("检查点赞数据是否以被缓存");
                    if(!redisUtil.exists("likes:isLikes")){
                        log.info("不存在，将点赞表缓存");
                        List<user_reply_likes> ll = userReplyLikesMapper.getAll();
                        for(user_reply_likes like : ll){
                            String key_ = "likes:reply:"+ like.getReplyId() +":user:"+ like.getUserId();
                            redisUtil.set(key_,like);
                        }
                        redisUtil.set("likes:reply:isLikes","true");
                    }
                    return pageResult;
                }
            }
            if(type == 3){//倒序查询
                if(redisUtil.exists(key1_3)){
                    log.info("查询到分页缓存");
                    List<ReplyDVO> replies = redisUtil.GetAll(key1_3);
                    PageResult pageResult = PageResult.builder()
                            .total(replies.size())
                            .records(replies)
                            .build();
                    log.info("检查点赞数据是否以被缓存");
                    if(!redisUtil.exists("likes:isLikes")){
                        log.info("不存在，将点赞表缓存");
                        List<user_reply_likes> ll = userReplyLikesMapper.getAll();
                        for(user_reply_likes like : ll){
                            String key_ = "likes:reply:"+ like.getReplyId() +":user:"+ like.getUserId();
                            redisUtil.set(key_,like);
                        }
                        redisUtil.set("likes:reply:isLikes","true");
                    }
                    return pageResult;
                }
            }

            //如果缓存不存在
            log.info("开始查询replies,user表,当前用户user ===》{}",BaseContext.getCurrentId());

            if(BaseContext.getCurrentId() == null){
                BaseContext.setCurrentId(id);
            }

            log.info("开始查询父回复");
            Page<ReplyDVO> page = new Page<>();
            if(type == 1){
                PageHelper.startPage(repliesPageDTO.getPage(), repliesPageDTO.getPageSize());
                page = repliesMapper.selectReliesbyPostId_hot(repliesPageDTO.getPostid());
            }
            else if(type == 2){
                PageHelper.startPage(repliesPageDTO.getPage(), repliesPageDTO.getPageSize());
                page = repliesMapper.selectReliesbyPostId_Proper(repliesPageDTO.getPostid());
            }
            else if(type == 3){
                PageHelper.startPage(repliesPageDTO.getPage(), repliesPageDTO.getPageSize());
                page = repliesMapper.selectReliesbyPostId_Reverse(repliesPageDTO.getPostid());
            }


            //父回复集合
            List<ReplyDVO> replies = page.getResult();
            log.info("开始查询子回复");

            for(ReplyDVO Father : replies){
                List<ReplyDVO> child = repliesMapper.selectChildReply(Father.getId());
                Father.setChildren(child);
            }


            PageResult pageResult = PageResult.builder()
                    .total(page.getTotal())
                    .records(replies)
                    .build();


            //缓存分页数据
            if(type == 1){
                redisUtil.pushAll(key1, replies,10, TimeUnit.MINUTES);
            }
            else if(type == 2){
                redisUtil.pushAll(key1_2, replies,10, TimeUnit.MINUTES);
            }
            else if(type == 3){
                redisUtil.pushAll(key1_3, replies,10, TimeUnit.MINUTES);
            }
            //缓存单条数据
            for (ReplyDVO replyDVO : replies) {
                String k = key2+replyDVO.getId();
                if(!redisUtil.exists(k)){
                    redisUtil.set(k,replyDVO);
                    if(replyDVO.getChildren().size()>0){
                        for(ReplyDVO child : replyDVO.getChildren()){
                            redisUtil.set(key2+child.getId(),child);
                        }
                    }
                }

            }

            return pageResult;
        }catch (Exception e){
            log.info("数据查询异常");
            return null;

        }
    }


    /**
     * 新增回复
     * @param newReplyDTO
     * @return
     */
    @Override
    @Transactional
    public Result addNewReply(NewReplyDTO newReplyDTO) {
        String key = "replies:postId:"+newReplyDTO.getPostId()+":replyId:";

        if(newReplyDTO.getContent() == null || newReplyDTO.getContent().equals("")){
            log.info("newReply: {}",newReplyDTO);
            return Result.error("内容不能为空");

        }
        Replies replies = Replies.builder()
                .content(newReplyDTO.getContent())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .postId(newReplyDTO.getPostId())
                .parentId(newReplyDTO.getParentId())
                .userId(BaseContext.getCurrentId())
                .status(0)
                .likeCount(0)
                .build();

        try{
            //新增回复
            repliesMapper.addReply(replies);
            log.info("新增reply成功");

            //更新post回复数,最后回复时间
            Posts post =  postsMapper.getPostById(newReplyDTO.getPostId());
            post.setReplyCount(post.getReplyCount()+1);
            post.setLastReplyTime(LocalDateTime.now());
            postsMapper.UpdataPost(post);
            //更新post缓存
            //清空分页缓存
            String k = "Posts:Sec:"+ post.getSectionId() +":page:*";
            Set<String> kk = redisUtil.keys(k);
            redisUtil.delete(kk.toArray(new String[0]));
            //更新单条缓存
            String k1 = "Posts:Sec:"+ post.getSectionId() +":postId:"+post.getId();
            redisUtil.set(k1,post);

            ReplyDVO replyDVO =  repliesMapper.getReplyById(replies.getId());

            delRepliesPage("replies:PostId:" + newReplyDTO.getPostId() + ":page:*");

            log.info("更新单条缓存");
            BeanUtils.copyProperties(replyDVO,replies);
            updateRedis("replies:PostId:"+newReplyDTO.getPostId()+":replyId:"+replies.getId(),replies);

            //发送系统消息
            String kkk = "user_Session:";
            InteractionNotification interactionNotification = new InteractionNotification();
            interactionNotification.setSenderId(Integer.parseInt(BaseContext.getCurrentId().toString()) );
            interactionNotification.setPostId(post.getId());

            if(newReplyDTO.getParentId().intValue() != 0){
                interactionNotification.setType(InteractionNotification.NotificationType.COMMENT_REPLY.getCode());
                interactionNotification.setReplyId(replies.getId());
                Long ReceiverId =  repliesMapper.getRepliesById(newReplyDTO.getParentId() ).getUserId();
                interactionNotification.setReceiverId(Integer.parseInt(ReceiverId.toString()));

            }else {
                interactionNotification.setType(InteractionNotification.NotificationType.POST_REPLY.getCode());
                interactionNotification.setReceiverId(Integer.parseInt(post.getUserId().toString()));

            }
            interactionNotificationsMapper.addNotifications(interactionNotification);

            kkk = kkk + interactionNotification.getReceiverId();
            if(redisUtil.exists(kkk)) {
                NotificationDTO notificationDTO = interactionNotificationsMapper.getSystemNotificationsById(interactionNotification.getId());
                brokerMessagingTemplate.convertAndSendToUser(
                        interactionNotification.getReceiverId().toString(),
                        "/queue/messages",
                        notificationDTO
                );
            }
            return Result.success(replyDVO);
        }catch (Exception e ){
            e.printStackTrace();
        }

        return Result.error("回复失败");
    }

    /**
     * 检查点赞状态
     * @param replyId
     * @return
     */
    @Override
    public Result<Boolean> checktoggleLike(Long replyId) {

        Long userId = BaseContext.getCurrentId();
        String key = "likes:reply:"+replyId+":user:"+userId;

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
     * 点赞操作
     * @param replyId
     * @return
     */
    @Override
    @Transactional
    public Result<InteractionNotification> doToggleLike(Long replyId,Long postId) {

        Long userId = BaseContext.getCurrentId();
        String key = "likes:reply:"+replyId+":user:"+userId;
        try{
            //更新点赞表
            userReplyLikesMapper.insert(userId,replyId);


            Replies replies = repliesMapper.getRepliesById(replyId);
            User user = userMapper.getUserById(replies.getUserId());

            //更新被点赞回复的获赞数
            replies.setLikeCount(replies.getLikeCount()+1);
            repliesMapper.updataReplies(replies);
            //更新回复缓存
            Set<String> kk = redisUtil.keys("replies:PostId:"+replies.getPostId()+":page:*");
            redisUtil.delete(kk.toArray(new String[0]));

            //更新被点赞人的获赞数
            user.setLikeCount(user.getLikeCount()+1);
            userMapper.updateUser(user);
            //更新被点赞人的缓存
            redisUtil.Hset("user:"+user.getId(),"likeCount",user.getLikeCount());

            //更新缓存
            redisUtil.set(key,"true");

            //发送点赞信息
            InteractionNotification interactionNotification = new InteractionNotification();
            interactionNotification.setReplyId(replyId);
            interactionNotification.setReceiverId(Integer.parseInt(user.getId().toString()) );
            interactionNotification.setSenderId(Integer.parseInt(userId.toString()) );
            interactionNotification.setType(InteractionNotification.NotificationType.REPLY_LIKE.getCode());
            interactionNotification.setPostId(postId);
            interactionNotificationsMapper.addNotifications(interactionNotification);
            return Result.success(interactionNotification);

        }catch (Exception e){
            return Result.error("点赞失败了");
        }
    }


    /**
     * 取消点赞
     * @param replyId
     * @return
     */
    @Override
    @Transactional
    public Result<Boolean> untoggleLike(Long replyId) {
        Long userId = BaseContext.getCurrentId();
        String key = "likes:reply:"+replyId+":user:"+userId;
        try{
            //删除缓存
            if(redisUtil.exists(key)){
                redisUtil.delete(key);
            }
            //删除点赞表
            userReplyLikesMapper.delete(userId,replyId);

            Replies replies = repliesMapper.getRepliesById(replyId);
            User user = userMapper.getUserById(replies.getUserId());
            //更新被点赞回复获赞数
            replies.setLikeCount(replies.getLikeCount()-1);
            repliesMapper.updataReplies(replies);
            //更新回复缓存
            Set<String> kk = redisUtil.keys("replies:PostId:"+replies.getPostId()+":page:*");
            redisUtil.delete(kk.toArray(new String[0]));
            //更新被点赞用户获赞数
            user.setLikeCount(user.getLikeCount()-1);
            userMapper.updateUser(user);
            //更新被点赞人的缓存
            redisUtil.Hset("user:"+user.getId(),"likeCount",user.getLikeCount());

            return Result.success(true);
        }catch (Exception e){
            return Result.error("取消点赞出错");
        }

    }

    public  void updateRedis(String itemKey, Replies replies){

            redisUtil.set(itemKey, replies);

    }
    public void delRepliesPage(String pattern){
        Set<String> keys = redisUtil.keys(pattern);
        if(keys.isEmpty()){
            return ;
        }
        redisUtil.delete(keys.toArray(new String[0]));
    }
}
