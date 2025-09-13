package com.cyj.xx.service;

import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.MeanUser;
import com.cyj.xx.dvo.UserInformationDVO;
import com.cyj.xx.dvo.UserLoginDVO;
import com.cyj.xx.result.Result;

public interface userService {

    public Result userEnroll(UserEnrollDTO userEnrollDTO);

    public UserLoginDVO userLogin(UserLoginDTO userLoginDTO);

    public Result userPersonal(String username);

    public Result updateUser(UserReviseDTO userReviseDTO);

    public Result changepwd(UserChangePwd userChangePwd);

    public MeanUser meanUser(Long id);


}
