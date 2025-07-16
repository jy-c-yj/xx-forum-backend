package com.cyj.xx.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecPostsPageDTO {
    private int page;
    private int pageSize;
    private Long sectionId = null;
}
