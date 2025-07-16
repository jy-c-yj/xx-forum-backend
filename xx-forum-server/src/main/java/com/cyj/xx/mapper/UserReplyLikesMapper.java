package com.cyj.xx.mapper;


import com.cyj.xx.dto.PostsPageDTO;
import com.cyj.xx.dto.UpdataReplyLikeDTO;
import com.cyj.xx.dvo.PostsHomeDVO;
import com.cyj.xx.entity.Posts;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserReplyLikesMapper {


    @Delete("delete from user_reply_likes where user_id = #{userId} and reply_id = #{replyId}")
    void delete(Long userId, Long replyId);


    @Insert("insert into user_reply_likes( user_id, reply_id) values (#{userId},#{replyId})")
    void insert(Long userId, Long replyId);
}
