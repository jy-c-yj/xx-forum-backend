package com.cyj.xx.dvo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInformationMyCollectionDVO {
    private Long id;                // 帖子ID
    private String title;           // 帖子标题
    private Long userId;         // 发帖用户ID
    private String userName;     // 发帖用户名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 创建时间

}
