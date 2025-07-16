package com.cyj.xx.dvo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdataReplyLikedDVO {
    private Integer likeCount;
    private Long replyId;
    private boolean liked = false;
}
