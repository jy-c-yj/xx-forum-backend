package com.cyj.xx.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkPrivateMsgReadDTO {
    private Long senderId;
    private Long receiverId;
}
