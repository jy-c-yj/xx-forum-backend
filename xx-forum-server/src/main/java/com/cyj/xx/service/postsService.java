package com.cyj.xx.service;

import com.cyj.xx.dto.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;

public interface postsService {

    PageResult page(PostsPageDTO postsPageDTO);

    Result postdetail(Long postid);

    Result addlikeCount(UpdataPostLikeDTO updataPostLikeDTO);

    Result addViewCount(Long id ,Integer viewCount);

    PageResult Secpage(SecPostsPageDTO secPostsPageDTO);
}
