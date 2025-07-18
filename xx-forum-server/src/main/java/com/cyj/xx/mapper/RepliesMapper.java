package com.cyj.xx.mapper;


import com.cyj.xx.dvo.ReplyDVO;
import com.cyj.xx.entity.Replies;
import com.cyj.xx.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface RepliesMapper {


    Page<ReplyDVO> selectReliesbyPostId(Integer postid,Long userid);

    void addReply(Replies replies);


    ReplyDVO getReplyById(Long id);

    @Select("SELECT * FROM replies where id = #{id}")
    Replies getRepliesById(Long id);

    void updataReplies(Replies replies);



    List<ReplyDVO> selectChildReply(Long replyid,Long userid);
}
