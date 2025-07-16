package com.cyj.xx.mapper;


import com.cyj.xx.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {


    void userEnroll(User user);

    @Select("SELECT * FROM users where username = #{userName}")
    User getUserByUserName(String userName);

    @Select("SELECT * FROM users where id = #{id}")
    User getUserById(Long id);

    void updateUser(User user);

}
