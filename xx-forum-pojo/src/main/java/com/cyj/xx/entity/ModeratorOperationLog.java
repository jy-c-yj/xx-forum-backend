package com.cyj.xx.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.cyj.xx.Enum.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModeratorOperationLog {
    /**
     * 日志ID
     */
    private Long id;

    /**
     * 版主用户ID
     */
    private Long moderatorId;

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 目标类型
     */
    private TargetType targetType;

    /**
     * 目标ID（帖子ID或用户ID）
     */
    private Long targetId;

    /**
     * 板块ID
     */
    private Long sectionId;

    /**
     * 操作原因
     */
    private String reason;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;



}
