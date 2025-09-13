package com.cyj.xx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.PrimitiveIterator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewReplyDTO {
    private String content;
    private Long postId;
    private Long parentId = null;
}
