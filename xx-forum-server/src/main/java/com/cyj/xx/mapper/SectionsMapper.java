package com.cyj.xx.mapper;



import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.dvo.SearchPostDVO;
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
    List<MySectionDVO> getMySection(Long id);

    @Select("SELECT * FROM sections WHERE id = #{id}")
    Section getSectionById(Long id);


    void UpdateSection(Section section);

    Page<SearchSectionDVO> searchSection(String keyword);

    @Insert("INSERT INTO sections( name, description, background_image,  admin_id,  image) values (#{name}, #{description}, #{backgroundImage},  #{adminId},  #{image})")
    void addNewSection(Section newSection);
}
