package com.cyj.xx.controller.user;


import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.RepliesServiceImpl;
import com.cyj.xx.service.repliesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class replyController {

    @Autowired
    private repliesService replyService;

    @GetMapping("/reply")
    public Result reply(RepliesPageDTO repliesPageDTO) {
        PageResult pageResult= replyService.getReply(repliesPageDTO);

        return Result.success(pageResult);
    }
    @PostMapping("/newReply")
    public Result newReply(@RequestBody NewReplyDTO newReplyDTO) {



        return replyService.addNewReply(newReplyDTO);
    }

    /**
     * 点赞
     * @param replyId
     * @return
     */

    @PostMapping("/doToggleLike/{replyId}")
    public Result<Boolean> doToggleLike(@PathVariable Long replyId) {
        return replyService.doToggleLike(replyId);
    }

    /**
     * 取消点赞
     * @param replyId
     * @return
     */

    @DeleteMapping("/untoggleLike")
    public Result<Boolean> untoggleLike(Long replyId) {
        return replyService.untoggleLike(replyId);
    }

    /**
     * 检查点赞状态
     * @param replyId
     * @return
     */
    @GetMapping("/checktoggleLike")
    public Result<Boolean> checktoggleLike(Long replyId) {
        return replyService.checktoggleLike(replyId);
    }



}
