package com.cyj.xx.mapper;


import com.cyj.xx.entity.sections_follows;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionFollowsMapper {

    @Insert("INSERT INTO section_follows(user_id, section_id) VALUES (#{userId},#{sectionId})")
    void followSec(Long userId, Long sectionId);

    @Delete("DELETE FROM section_follows where section_id = #{sectionId} and user_id = #{userId}")
    void unfollowSec(Long userId, Long sectionId);

    @Select("SELECT * FROM section_follows where user_id = #{userId}")
    List<sections_follows> findMySections(Long userId);
}
