package com.cyj.xx.dvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchSectionDVO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer followersCount;
    private Integer postCount;
    private Boolean isFollowed;
}
