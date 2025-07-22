package com.cyj.xx.controller.user;


import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.SecPostsPageDTO;
import com.cyj.xx.dto.UpdataPostLikeDTO;
import com.cyj.xx.dvo.ReplyDVO;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.PostsServiceImpl;
import com.cyj.xx.service.Impl.RepliesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class PostsController {

    @Autowired
    PostsServiceImpl postsService;


    /**
     * 分页查询
     * @return
     */
    @GetMapping("/posts")
    public Result posts(PostsPageDTO postsPageDTO) {

        PageResult pageResult = postsService.page(postsPageDTO);

        return Result.success(pageResult);

    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("/Secposts")
    public Result Secposts(SecPostsPageDTO secPostsPageDTO) {

        PageResult pageResult = postsService.Secpage(secPostsPageDTO);

        return Result.success(pageResult);

    }


    /**
     * 查看帖子详情
     * @param postid
     * @return
     */
    @GetMapping("/postdetail")
    public Result postdetail(Long postid) {


        return postsService.postdetail(postid);

    }

    /**
     * 增加帖子浏览数
     * @param id
     * @return
     */
    @GetMapping("/addPostView")
    public Result addPostView(Long id ,Integer viewCount) {

        return postsService.addViewCount(id ,viewCount);
    }


    /**
     * 点赞帖子
     * @param postId
     * @return
     */


    @PostMapping("/likePost/{postId}")
    public Result<Boolean> likePost(@PathVariable Long postId) {


        return  postsService.likePost(postId);
    }

    /**
     * 取消点赞
     * @param postId
     * @return
     */

    @DeleteMapping("/unlikePost")
    public Result<Boolean> unlikePost(Long postId) {
        return postsService.unlikePost(postId);
    }

    /**
     * 检查点赞状态
     * @param postId
     * @return
     */
    @GetMapping("/checkLike")
    public Result<Boolean> checkLike(Long postId) {

        return postsService.checkLike(postId);
    }

    /**
     * 收藏帖子
     * @param postId
     * @return
     */


    @PostMapping("/collectPost/{postId}")
    public Result<Boolean> collectPost(@PathVariable Long postId) {


        return  postsService.collectPost(postId);
    }

    /**
     * 取消收藏
     * @param postId
     * @return
     */

    @DeleteMapping("/uncollectPost")
    public Result<Boolean> uncollectPost(Long postId) {
        return postsService.uncollectPost(postId);
    }

    /**
     * 检查收藏状态
     * @param postId
     * @return
     */
    @GetMapping("/checkCollect")
    public Result<Boolean> checkCollect(Long postId) {

        return postsService.checkCollect(postId);
    }



}
