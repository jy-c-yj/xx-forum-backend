package com.cyj.xx.mapper;


import com.cyj.xx.entity.user_post_collection;
import com.cyj.xx.entity.user_post_likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserPostCollectionMapper {


    @Delete("delete from user_post_collections where user_id = #{userId} and post_id = #{postId}")
    void delete(Long userId, Long postId);


    @Insert("insert into user_post_collections( user_id, post_id) values (#{userId},#{postId})")
    void insert(Long userId, Long postId);

    @Select("SELECT * FROM user_post_collections")
    List<user_post_collection> getAll();

    @Select("SELECT  * FROM user_post_collections WHERE user_id = #{userId} and post_id = #{postId}")
    user_post_collection getlike(Long postId, Long userId);
}
