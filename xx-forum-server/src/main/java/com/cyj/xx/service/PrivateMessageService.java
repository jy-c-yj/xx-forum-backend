package com.cyj.xx.service;

import com.cyj.xx.dto.MarkPrivateMsgReadDTO;
import com.cyj.xx.entity.PrivateMessage;
import com.cyj.xx.entity.admin;
import com.cyj.xx.result.Result;

public interface PrivateMessageService {


    Result getContact();

    Result getMessage(Long contactId);

    void addMessage(PrivateMessage privateMessage);

    Result MarkAsRead(MarkPrivateMsgReadDTO markPrivateMsgReadDTO);
}
