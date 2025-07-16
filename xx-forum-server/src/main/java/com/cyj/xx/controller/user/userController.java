package com.cyj.xx.controller.user;


import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.UserLoginDVO;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    UserServiceImpl userService;


    /**
     * 用户登陆
     * @param userloginDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userloginDTO) {

        UserLoginDVO userloginDVO = userService.userLogin(userloginDTO);




        return Result.success(userloginDVO);
    }

    /**
     * 用户注册
     * @param
     * @return
     */

    @PostMapping("/enroll")
    public Result enroll(String username,String password ,String email, MultipartFile image) {


        UserEnrollDTO userenrollDTO = UserEnrollDTO.builder()
                .username(username)
                .password(password)
                .email(email)
                .file(image)
                .build();



        return userService.userEnroll(userenrollDTO);
    }

    /**
     * 修改user信息
     * @param userReviseDTO
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody UserReviseDTO userReviseDTO) {

        return userService.updateUser(userReviseDTO);
    }

    /**
     * 显示个人信息
     * @param username
     * @return
     */
    @GetMapping("/personal")
    public Result personal(String username) {

        return userService.userPersonal(username);

    }


    /**
     * 修改密码
     * @return
     */
    @PostMapping("/changepwd")
    public Result changePwd (@RequestBody UserChangePwd userChangePwd) {



        return userService.changepwd(userChangePwd);
    }

}
