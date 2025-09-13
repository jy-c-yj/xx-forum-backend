package com.cyj.xx.mapper;


import com.cyj.xx.entity.BannedPost;
import com.cyj.xx.entity.BannedUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannedUserMapper {
    void insert(BannedUser bannedUser);
}
