package com.cyj.xx.mapper;


import com.cyj.xx.dvo.BanUserLogDVO;
import com.cyj.xx.entity.BannedPost;
import com.cyj.xx.entity.BannedUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannedUserMapper {
    void insert(BannedUser bannedUser);

    List<BanUserLogDVO> selectBanUserLog(Long sectionId);

    @Delete("DELETE FROM banned_users where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM banned_users where id = #{id}")
    BannedUser getById(Long id);

    @Select("SELECT * FROM banned_users where user_id = #{userId} and section_id = #{sectionId}")
    BannedUser isBanned(Long userId, Long sectionId);
}
