package com.cyj.xx.service;

import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.RepliesPageDTO;

import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;


public interface repliesService {


    PageResult getReply(RepliesPageDTO repliesPageDTO);

    Result addNewReply(NewReplyDTO newReplyDTO);

    Result UpdataReplyLike(UpdataReplyLikeDTO updataReplyLikeDTO);
}
