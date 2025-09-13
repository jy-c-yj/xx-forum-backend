package com.cyj.xx.mapper;


import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.entity.InteractionNotification;
import com.cyj.xx.entity.UserSignIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserSignInMapper {

    void addNewUserSignIn(UserSignIn userSignIn);

    void UnUserSignIn(Long userId, Long sectionId);

    void deleteUserSignIn(Long userId, Long sectionId);

    @Select("SELECT * FROM user_sign_in where user_id = #{userId} and section_id = #{sectionId}")
    UserSignIn getUserSignIn(Long sectionId, Long userId);

    @Select("SELECT * FROM user_sign_in where id = #{userSignInId}")
    UserSignIn selectUserSignIn(Long userSignInId);


    void UpdateUserSignIn(UserSignIn userSignIn);

    void BatchSignInStatus();

    @Select("SELECT * FROM user_sign_in where user_id = #{userId}")
    List<UserSignIn> getUserSignInByUserId(Long userId);
}
