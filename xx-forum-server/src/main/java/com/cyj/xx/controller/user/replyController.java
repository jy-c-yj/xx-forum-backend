package com.cyj.xx.controller.user;


import com.cyj.xx.dto.NewReplyDTO;
import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.dto.RepliesPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.entity.InteractionNotification;
import com.cyj.xx.mapper.InteractionNotificationsMapper;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.RepliesServiceImpl;
import com.cyj.xx.service.repliesService;
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class replyController {

    @Autowired
    private repliesService replyService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private InteractionNotificationsMapper interactionNotificationsMapper;

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    /**
     * 获取回复
     * @param repliesPageDTO
     * @return
     */
    @GetMapping("/reply")
    public Result reply(RepliesPageDTO repliesPageDTO) {
        PageResult pageResult= replyService.getReply(repliesPageDTO);
        if(pageResult == null){
            return Result.error("请求错误");
        }
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

    @PostMapping("/doToggleLike/{postId}/{replyId}")
    public Result<Boolean> doToggleLike(@PathVariable Long replyId,
                                        @PathVariable Long postId) {
        Result<InteractionNotification> result = replyService.doToggleLike(replyId,postId);

            InteractionNotification in = result.getData();


        String k = "user_Session:"+in.getReceiverId();
        Long iid = in.getId();
        System.out.println("11111111111111111111111111111111111111111111========"+iid);

        if(redisUtil.exists(k)){
            NotificationDTO notificationDTO = interactionNotificationsMapper.getSystemNotificationsById(iid);
            brokerMessagingTemplate.convertAndSendToUser(
                    in.getReceiverId().toString(),
                    "/queue/messages",
                    notificationDTO
            );
        }
        return Result.success(true);
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
