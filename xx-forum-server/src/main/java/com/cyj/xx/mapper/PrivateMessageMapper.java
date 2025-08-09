package com.cyj.xx.mapper;


import com.cyj.xx.dvo.PrivateContactDVO;
import com.cyj.xx.dvo.PrivateMessageDVO;
import com.cyj.xx.entity.PrivateMessage;
import com.cyj.xx.entity.user_reply_likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PrivateMessageMapper {


    List<PrivateContactDVO> getContact(Long userId);

    List<PrivateMessageDVO> getMessage(Long userId, Long contactId);

    void addMessage(PrivateMessage privateMessage);

    void updatePrivateMessage(PrivateMessage privateMessage);
}
