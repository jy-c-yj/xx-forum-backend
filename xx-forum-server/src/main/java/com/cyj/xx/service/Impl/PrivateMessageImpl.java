package com.cyj.xx.service.Impl;

import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.MarkPrivateMsgReadDTO;
import com.cyj.xx.dvo.PrivateContactDVO;
import com.cyj.xx.dvo.PrivateMessageDVO;
import com.cyj.xx.entity.PrivateMessage;
import com.cyj.xx.entity.admin;
import com.cyj.xx.mapper.PrivateMessageMapper;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.PrivateMessageService;
import com.cyj.xx.service.adminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PrivateMessageImpl implements PrivateMessageService {


    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    @Override
    public Result getContact() {

        Long userId = BaseContext.getCurrentId();

        List<PrivateContactDVO> ll = privateMessageMapper.getContact(userId);

        return Result.success(ll);
    }


    @Override
    public Result getMessage(Long contactId) {
        Long userId = BaseContext.getCurrentId();

        List<PrivateMessageDVO> ll = privateMessageMapper.getMessage(userId,contactId);

        return Result.success(ll);
    }

    @Override
    public void addMessage(PrivateMessage privateMessage) {
        if(privateMessage == null) {
            return;
        }
        privateMessageMapper.addMessage(privateMessage);
    }

    /**
     * 设置已读
     * @param markPrivateMsgReadDTO
     * @return
     */
    @Override
    public Result MarkAsRead(MarkPrivateMsgReadDTO markPrivateMsgReadDTO) {
        try{
            PrivateMessage privateMessage = new PrivateMessage();
            privateMessage.setReceiverId(markPrivateMsgReadDTO.getReceiverId());
            privateMessage.setSenderId(markPrivateMsgReadDTO.getSenderId());
            privateMessage.setIsRead(true);
            privateMessageMapper.updatePrivateMessage(privateMessage);
            return Result.success();

        }catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
