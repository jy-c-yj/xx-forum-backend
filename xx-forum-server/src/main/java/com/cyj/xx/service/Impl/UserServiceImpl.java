package com.cyj.xx.service.Impl;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.MeanUser;
import com.cyj.xx.dvo.UserInformationDVO;
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
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements userService {


    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    RedisUtil redisUtil;



    /**
     * 用户登陆
     * @param userLoginDTO
     */
    @Override
    public UserLoginDVO userLogin(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

//        redisTemplate.opsForHash().get()

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
                .id(user.getId())
                .file(user.getImage())
                .status(user.getStatus())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .desc(user.getDesc())
                .gender(user.getGender())
                .token(jwt)
                .likeCount(user.getLikeCount())
                .postCount(user.getPostCount())
                .build();
        String key = "user:"+user.getId();



        if (!redisUtil.exists(key)) {
            log.info("向redis缓存用户 ===> {} ", user.getId());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", userdVO.getId());
            userMap.put("file", userdVO.getFile());
            userMap.put("status", userdVO.getStatus());
            userMap.put("email", userdVO.getEmail());
            userMap.put("username", userdVO.getUsername());
            userMap.put("phone", userdVO.getPhone());
            userMap.put("desc", userdVO.getDesc());
            userMap.put("gender", userdVO.getGender());
            userMap.put("likeCount", userdVO.getLikeCount());
            userMap.put("postCount", userdVO.getPostCount());
            redisUtil.hsave(key, userMap,1 ,TimeUnit.DAYS);
        }



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
                    .phone("未绑定")
                    .desc("这个人很懒，什么都没写")
                    .build();
        }
        else {
            throw new RuntimeException();
        }

        userMapper.userEnroll(user);




        return Result.success(user);


    }

    /**
     * 菜单处信息显示
     * @param id
     */
    @Override
    public MeanUser meanUser(Long id) {
        String key = "user:"+id;
        MeanUser meanUser = new MeanUser();
        if (redisUtil.exists(key)) {
            Map<String,Object> map = redisUtil.hgetAll(key);
            meanUser.setId(Long.parseLong(map.get("id").toString()));
            meanUser.setUsername(map.get("username").toString());
            meanUser.setImage(map.get("file").toString());
            return meanUser;
        }

        User  u = userMapper.getUserById(id);
        meanUser.setId(u.getId());
        meanUser.setUsername(u.getUsername());
        meanUser.setImage(u.getImage());
        return meanUser;

    }

    /**
     * 主页部分个人信息显示
     * @param username
     * @return
     */
    @Override
    public Result userPersonal(String username) {
        Map<String,Object> map = new HashMap<>();
        User user = new User();

        if(username == null || "".equals(username)) {
            return Result.error("未登录");
        }
        Long id = BaseContext.getCurrentId();
        if(redisUtil.exists("user:"+id)){

            map = redisUtil.hgetAll("user:"+id);
            log.info("查询缓存 ==== 》{}",id);
            UserPersonalDVO userpersonalDVO = UserPersonalDVO.builder()
                    .id(id)
                    .username(map.get("username").toString())
                    .image(map.get("file").toString())
                    .phone(map.get("phone").toString())
                    .email(map.get("email").toString())
                    .likeCount((Integer) map.get("likeCount"))
                    .postCount((Integer) map.get("postCount"))
                    .desc(map.get("desc").toString())
                    .gender(map.get("gender").toString())
                    .build();

            return Result.success(userpersonalDVO);
        }
        log.info("查询数据库 ==== 》{}",id);
        user =  userMapper.getUserByUserName(username);
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
            return Result.error("修改失败，未获取到用户数据");
        }

        try{
            log.info("正在进行属性复制");
            BeanUtils.copyProperties(userReviseDTO,user );
            user.setId(BaseContext.getCurrentId());
            log.info(user.toString());
            userMapper.updateUser(user);
            log.info("修改完成，开始修改缓存");
            User u = userMapper.getUserById(BaseContext.getCurrentId());
            String key = "user:"+u.getId();
            Map<String,Object> map = new HashMap<>();
            map.put("username",u.getUsername());
            map.put("image",u.getImage());
            map.put("phone",u.getPhone());
            map.put("email",u.getEmail());
            map.put("likeCount",u.getLikeCount());
            map.put("postCount",u.getPostCount());
            map.put("desc",u.getDesc());
            map.put("gender",u.getGender());
            map.put("id",u.getId());
            map.put("status",u.getStatus());
            if(redisUtil.exists(key)){
                redisUtil.hsave(key,map);
                log.info("缓存完成");
            }



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
