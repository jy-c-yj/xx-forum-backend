package com.cyj.xx.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostPushDTO {
    private Integer SectionId;
    private String title;
    private String content;
}
