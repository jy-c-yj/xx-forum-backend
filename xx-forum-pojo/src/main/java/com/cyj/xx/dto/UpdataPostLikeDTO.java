package com.cyj.xx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdataPostLikeDTO {
    private Long postId;
    private Integer likeCount;
    private String username;
    private boolean liked = false;


}
