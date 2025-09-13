package com.cyj.xx.mapper;



import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MyFollowSectionDVO;
import com.cyj.xx.dvo.MySectionsDVO;
import com.cyj.xx.dvo.SearchSectionDVO;
import com.cyj.xx.entity.Section;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface SectionsMapper {


    List<HotSectionsDVO> PageHotSec();


    @Select("SELECT id,name,image FROM sections\n" +
            "\n" +
            "WHERE id IN (SELECT section_id FROM section_follows WHERE user_id = #{id})")
    List<MyFollowSectionDVO> getFollowMySection(Long id);

    @Select("SELECT * FROM sections WHERE id = #{id}")
    Section getSectionById(Long id);


    void UpdateSection(Section section);

    Page<SearchSectionDVO> searchSection(String keyword);

    @Insert("INSERT INTO sections( name, description, background_image,  admin_id,  image) values (#{name}, #{description}, #{backgroundImage},  #{adminId},  #{image})")
    void addNewSection(Section newSection);

    @Select("SELECT * FROM  sections where admin_id = #{userId}")
    List<Section> getSection(Long userId);
}
