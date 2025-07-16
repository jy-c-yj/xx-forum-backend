package com.cyj.xx.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepliesPageDTO {
    private int page;
    private int pageSize;
    private int postid;
}
