package com.cyj.xx.controller.user;


import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.RepliesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class replyController {

    @Autowired
    private RepliesServiceImpl replyService;

    @GetMapping("/reply")
    public Result reply(RepliesPageDTO repliesPageDTO) {
        PageResult pageResult= replyService.getReply(repliesPageDTO);

        return Result.success(pageResult);
    }
    @PostMapping("/newReply")
    public Result newReply(@RequestBody NewReplyDTO newReplyDTO) {



        return replyService.addNewReply(newReplyDTO);
    }

    @PostMapping("/UpdataReplyLike")
    public Result UpdataReplyLike(@RequestBody UpdataReplyLikeDTO updataReplyLikeDTO) {
        return replyService.UpdataReplyLike(updataReplyLikeDTO);
    }



}
