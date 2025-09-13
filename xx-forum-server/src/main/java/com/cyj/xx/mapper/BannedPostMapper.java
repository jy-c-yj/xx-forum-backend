package com.cyj.xx.mapper;


import com.cyj.xx.entity.BannedPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannedPostMapper {
    void insert(BannedPost bannedPost);
}
