package com.cyj.xx.mapper;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPostLikesMapper {


    @Delete("delete from user_post_likes where user_id = #{userId} and post_id = #{postId}")
    void delete(Long userId, Long postId);


    @Insert("insert into user_post_likes( user_id, post_id) values (#{userId},#{postId})")
    void insert(Long userId, Long postId);
}
