package com.cyj.xx.service.Impl;

import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.dvo.ReplyDVO;
import com.cyj.xx.dvo.UpdataReplyLikedDVO;
import com.cyj.xx.entity.Posts;
import com.cyj.xx.entity.Replies;
import com.cyj.xx.entity.User;
import com.cyj.xx.mapper.PostsMapper;
import com.cyj.xx.mapper.RepliesMapper;
import com.cyj.xx.mapper.UserMapper;
import com.cyj.xx.mapper.UserReplyLikesMapper;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.repliesService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


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

    @Override
    public PageResult getReply(RepliesPageDTO repliesPageDTO) {
        Long id = Long.parseLong("-1");
        try{

            log.info("开始查询replies,user表,当前用户user ===》{}",BaseContext.getCurrentId());

            if(BaseContext.getCurrentId() == null){
                BaseContext.setCurrentId(id);
            }

            log.info("开始查询父回复");
            PageHelper.startPage(repliesPageDTO.getPage(), repliesPageDTO.getPageSize());
            Page<ReplyDVO> page = repliesMapper.selectReliesbyPostId(repliesPageDTO.getPostid(),BaseContext.getCurrentId());

            //父回复集合
            List<ReplyDVO> replies = page.getResult();
            log.info("开始查询子回复");

            for(ReplyDVO Father : replies){
                List<ReplyDVO> child = repliesMapper.selectChildReply(Father.getId(),BaseContext.getCurrentId());
                Father.setChildren(child);
            }


            PageResult pageResult = PageResult.builder()
                    .total(page.getTotal())
                    .records(replies)
                    .build();

            return pageResult;
        }catch (Exception e){
            log.info("数据查询异常");
            Result.error("数据异常");
            e.printStackTrace();
        }




        return null;
    }


    /**
     * 新增回复
     * @param newReplyDTO
     * @return
     */
    @Override
    @Transactional
    public Result addNewReply(NewReplyDTO newReplyDTO) {

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
            Posts post =  postsMapper.getPostById(newReplyDTO.getPostId());
            post.setReplyCount(post.getReplyCount()+1);
            postsMapper.UpdataPost(post);
            ReplyDVO replyDVO =  repliesMapper.getReplyById(replies.getId());
            return Result.success(replyDVO);
        }catch (Exception e ){
            e.printStackTrace();
        }

        return Result.error("回复失败");
    }

    /**
     * 点赞功能
     * @param updataReplyLikeDTO
     * @return
     */
    @Override
    @Transactional
    public Result UpdataReplyLike(UpdataReplyLikeDTO updataReplyLikeDTO) {
        log.info("正在运行UpdataReplyLike，形参updataReplyLikeDTO===》{}",updataReplyLikeDTO);
        Replies replies = new Replies();
        replies.setId(updataReplyLikeDTO.getId());
        replies.setLikeCount(updataReplyLikeDTO.getLikeCount());
        UpdataReplyLikedDVO updataReplyLikedDVO = new UpdataReplyLikedDVO();

        Integer likeCount = updataReplyLikeDTO.getLikeCount();
        try {
            User user = userMapper.getUserById(updataReplyLikeDTO.getUserId());
            if(updataReplyLikeDTO.isLiked()){
                log.info("执行取消点赞操作replies.getLikeCount()====>{}",replies.getLikeCount());
                //执行取消点赞操作
                replies.setLikeCount(replies.getLikeCount()-1);
                log.info("执行取消点赞操作replies.getLikeCount()====>{}",replies.getLikeCount());
                user.setLikeCount(user.getLikeCount()-1);
                userReplyLikesMapper.delete(BaseContext.getCurrentId(),updataReplyLikeDTO.getId());
                likeCount--;

            }else {
                log.info("执行点赞操作replies.getLikeCount()====>{}",replies.getLikeCount());
                //执行点赞操作
                replies.setLikeCount(replies.getLikeCount()+1);
                log.info("执行赞操作replies.getLikeCount()====>{}",replies.getLikeCount());
                user.setLikeCount(user.getLikeCount()+1);
                userReplyLikesMapper.insert(BaseContext.getCurrentId(),updataReplyLikeDTO.getId());
                updataReplyLikedDVO.setLiked(true);
                likeCount++;
            }
            repliesMapper.updataReplies(replies);
            userMapper.updateUser(user);
            updataReplyLikedDVO.setReplyId(updataReplyLikeDTO.getId());
            updataReplyLikedDVO.setLikeCount(likeCount);

            return Result.success(updataReplyLikedDVO);
        }catch (Exception e){
            e.printStackTrace();
        }



        return Result.error("操作失败");
    }
}
