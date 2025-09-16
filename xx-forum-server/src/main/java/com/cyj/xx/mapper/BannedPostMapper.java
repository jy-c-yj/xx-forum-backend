package com.cyj.xx.mapper;


import com.cyj.xx.dvo.BanPostLogDVO;
import com.cyj.xx.entity.BannedPost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannedPostMapper {
    void insert(BannedPost bannedPost);

    List<BanPostLogDVO> selectBanPostLog(Long sectionId);

    @Delete("DELETE FROM banned_posts where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM banned_posts where id = #{id}")
    BannedPost getById(Long id);
}
