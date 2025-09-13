package com.cyj.xx.service;

import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.RepliesPageDTO;

import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.entity.InteractionNotification;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;


public interface repliesService {




    Result addNewReply(NewReplyDTO newReplyDTO);


    Result<Boolean> checktoggleLike(Long replyId);

    Result<InteractionNotification> doToggleLike(Long replyId, Long postId);

    Result<Boolean> untoggleLike(Long replyId);


    PageResult getReply(RepliesPageDTO repliesPageDTO);
}
