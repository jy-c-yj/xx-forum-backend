package com.cyj.xx.service.Impl;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.UserLoginDVO;
import com.cyj.xx.dvo.UserPersonalDVO;
import com.cyj.xx.entity.User;
import com.cyj.xx.exception.PasswordErrorException;
import com.cyj.xx.mapper.UserMapper;
import com.cyj.xx.properties.JwtProperties;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.userService;
import com.cyj.xx.utils.AliOssUtil;
import com.cyj.xx.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements userService {


    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtProperties jwtProperties;



    /**
     * 员工登陆
     * @param userLoginDTO
     */
    @Override
    public UserLoginDVO userLogin(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userMapper.getUserByUserName(username);

        //log.info(user.toString());

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException("PASSWORD_ERROR");
        }

        //获取jwt令牌

        HashMap map = new HashMap();
        map.put("username", user.getUsername());
        map.put("Id", user.getId());
        String jwt = JwtUtil.createJwt(jwtProperties.getSecret(), jwtProperties.getExptime(), map);



        UserLoginDVO userdVO = UserLoginDVO.builder()
                .file(user.getImage())
                .createTime(user.getCreatedTime())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .token(jwt)
                .likeCount(user.getLikeCount())
                .postCount(user.getPostCount())
                .build();

        return userdVO;


    }

    /**
     * 员工注册
     * @param userenrollDTO
     */

    @Override
    public Result userEnroll(UserEnrollDTO userenrollDTO) {

        String image=null;
        User user=null;
        User bol = userMapper.getUserByUserName(userenrollDTO.getUsername());

        if(bol!=null){
            return Result.error("用户名重复");
        }
        if(userenrollDTO!=null){

            try {
                //获取原始文件名
                String s = userenrollDTO.getFile().getOriginalFilename();
                log.info(s);
                //截取文件后缀
                String suffix = s.substring(s.lastIndexOf("."));
                log.info(suffix);
                //拼接文件名
                String newFileName = UUID.randomUUID().toString()+suffix;
                log.info(newFileName);
                //上传
                String url = AliOssUtil.Upload(userenrollDTO.getFile().getBytes(), newFileName);
                log.info(url);
                image = url;

            } catch (IOException e) {

                log.info("文件上传异常{}", e.getMessage());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            user = User.builder()
                    .createdTime(LocalDateTime.now())
                    .updatedTime(LocalDateTime.now())
                    .username(userenrollDTO.getUsername())
                    .password(userenrollDTO.getPassword())
                    .email(userenrollDTO.getEmail())
                    .image(image)
                    .status(1)
                    .build();
        }
        else {
            throw new RuntimeException();
        }

        userMapper.userEnroll(user);




        return Result.success(user);


    }

    /**
     * 个人信息显示
     * @param username
     * @return
     */
    @Override
    public Result userPersonal(String username) {

        if(username == null || "".equals(username)) {
            return Result.error("未登录");
        }

        User user =  userMapper.getUserByUserName(username);
        UserPersonalDVO userpersonalDVO = UserPersonalDVO.builder()
                .id(user.getId())
                .createtime(user.getCreatedTime())
                .username(user.getUsername())
                .email(user.getEmail())
                .likeCount(user.getLikeCount())
                .postCount(user.getPostCount())
                .image(user.getImage())
                .phone(user.getPhone())
                .desc(user.getDesc())
                .gender(user.getGender())
                .build();


        return Result.success(userpersonalDVO);
    }

    /**
     * 修改用户个人信息
     * @param userReviseDTO
     */
    @Override
    public Result updateUser(UserReviseDTO userReviseDTO) {
        User user = new User();
        String image=null;
        if(userReviseDTO == null ) {
            log.info("userReviseDTO为空null");
            return Result.error("修改失败，为获取到用户数据");
        }

        try{
            log.info("正在进行属性复制");
            BeanUtils.copyProperties(userReviseDTO,user );
            user.setId(BaseContext.getCurrentId());
            log.info(user.toString());
            userMapper.updateUser(user);
            log.info("修改完成");
        }catch (Exception e) {

            return Result.error("修改失败");
        }

        return Result.success();
    }

    /**
     * 修改密码
     * @param userchangePwd
     * @return
     */
    @Override
    public Result changepwd(UserChangePwd userchangePwd) {

        User user = userMapper.getUserById(BaseContext.getCurrentId());
        String password = user.getPassword();
        String oldpassword = DigestUtils.md5DigestAsHex(userchangePwd.getOldPassword().getBytes());
        if(!password.equals(oldpassword)) {
            log.info("旧密码错误");
            return Result.error("旧密码错误");

        }
        log.info("oldpassword验证成功，开始修改");
        String newpassword = DigestUtils.md5DigestAsHex(userchangePwd.getNewPassword().getBytes());
        User user_ = User.builder().password(newpassword).id(BaseContext.getCurrentId()).build();
        log.info(user_.toString());
        userMapper.updateUser(user_);
        log.info("修改成功{}",user.toString());
        return Result.success();

    }
}
