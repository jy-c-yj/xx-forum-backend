package com.cyj.xx.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostsPageDTO {
    private int page;
    private int pageSize;
}
