package com.cyj.xx.service;

import com.cyj.xx.dvo.UserSignInDVO;
import com.cyj.xx.entity.admin;
import com.cyj.xx.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



public interface UserSignInService {


    Result<UserSignInDVO> getUserSignIn(Long sectionId, Long userId);

    Result doUserSignIn(Long userSignInId);

    void BatchSignInStatus();

}
