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
public class OperationLogDVO {
    /**
     * 日志ID
     */
    private Long id;

    /**
     * 操作类型（枚举转换为字符串）
     */
    private String operationType;

    /**
     * 目标类型（枚举转换为字符串）
     */
    private String targetType;

    /**
     * 操作目标（帖子标题或用户名）
     */
    private String operationTarget;

    /**
     * 所属板块名称
     */
    private String sectionName;

    /**
     * 操作原因
     */
    private String reason;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
