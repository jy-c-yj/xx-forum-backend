package com.cyj.xx.dvo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdataPostLikedDVO {
    private Integer likeCount;
    private boolean liked = false;
}
