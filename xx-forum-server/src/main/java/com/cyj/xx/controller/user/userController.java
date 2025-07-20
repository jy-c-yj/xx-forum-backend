package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.MeanUser;
import com.cyj.xx.dvo.UserLoginDVO;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.UserServiceImpl;
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
@Slf4j
public class userController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    RedisUtil redisUtil;


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
     * 退出登陆操作
     * @return
     */

    @DeleteMapping("/quit")
    public  Result quit(){

        Long id = BaseContext.getCurrentId();

        String key = "user:"+id;
        log.info("用户{}退出登陆",key);
        boolean bol =  redisUtil.delete(key);
        if(!bol){
            return Result.error("操作出错，请检查登陆");
        }
        return Result.success();

    }

    /**
     * 菜单处信息显示
     * @return
     */
    @GetMapping("/meanUser")
    public Result meanUser(){
        if(BaseContext.getCurrentId()==null){
            return Result.error("检查登陆");
        }
        Long id = BaseContext.getCurrentId();
        MeanUser mu =userService.meanUser(id);
        return Result.success(mu);
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
