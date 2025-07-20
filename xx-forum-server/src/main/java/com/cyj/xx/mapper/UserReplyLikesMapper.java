package com.cyj.xx.mapper;


import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.dvo.PostsHomeDVO;
import com.cyj.xx.entity.Posts;
import com.cyj.xx.entity.user_reply_likes;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserReplyLikesMapper {


    @Delete("delete from user_reply_likes where user_id = #{userId} and reply_id = #{replyId}")
    void delete(Long userId, Long replyId);


    @Insert("insert into user_reply_likes( user_id, reply_id) values (#{userId},#{replyId})")
    void insert(Long userId, Long replyId);


    @Select("SELECT * FROM user_reply_likes")
    List<user_reply_likes> getAll();
}
