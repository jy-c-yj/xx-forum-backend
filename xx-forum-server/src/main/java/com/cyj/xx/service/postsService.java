package com.cyj.xx.service;

import com.cyj.xx.dto.*;
import com.cyj.xx.dvo.BanPostLogDVO;
import com.cyj.xx.entity.Posts;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;

import java.util.List;

public interface postsService {

    PageResult page(PostsPageDTO postsPageDTO);

    Result postdetail(Long postid);


    Result addViewCount(Long id ,Integer viewCount);

    PageResult Secpage(SecPostsPageDTO secPostsPageDTO);

    Result<Boolean> likePost(Long postId);

    Result<Boolean> unlikePost(Long postId);

    Result<Boolean> checkLike(Long postId);

    Result<Boolean> collectPost(Long postId);

    Result<Boolean> uncollectPost(Long postId);

    Result<Boolean> checkCollect(Long postId);

    Result<Posts> pushPost(PostPushDTO postPushDTO);

    Result<PageResult> searchPost(String keyword, int page, int size);

    Result<PageResult> searchPostInsec(String keyword, int page, int size, int sectionId);

    Result<Boolean> confirmBanPost(ConfirmBanPostDTO confirmBanPostDTO);

    Result<Boolean> pinPost(Long userId, Long postId);

    Result<Boolean> unpinPost(Long userId, Long postId);

    Result<List<BanPostLogDVO>> getBanPostLog(Long sectionId);

    Result<Boolean> unBanPost(Long id);
}
