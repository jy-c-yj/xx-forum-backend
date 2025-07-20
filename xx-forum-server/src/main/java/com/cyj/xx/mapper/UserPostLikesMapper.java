package com.cyj.xx.mapper;


import com.cyj.xx.entity.user_post_likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserPostLikesMapper {


    @Delete("delete from user_post_likes where user_id = #{userId} and post_id = #{postId}")
    void delete(Long userId, Long postId);


    @Insert("insert into user_post_likes( user_id, post_id) values (#{userId},#{postId})")
    void insert(Long userId, Long postId);

    @Select("SELECT * FROM user_post_likes")
    List<user_post_likes> getAll();

    @Select("SELECT  * FROM user_post_likes WHERE user_id = #{userId} and post_id = #{postId}")
    user_post_likes getlike(Long postId, Long userId);
}
