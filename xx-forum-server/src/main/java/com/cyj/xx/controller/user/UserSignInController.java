package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dvo.UserSignInDVO;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.UserSignInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/SignIn")
public class UserSignInController {

    @Autowired
    UserSignInService userSignInService;


    @GetMapping("/{id}")
    public Result<UserSignInDVO> getUserSignIn(@PathVariable("id") Long id) {
        Long userId = BaseContext.getCurrentId();
        return userSignInService.getUserSignIn(id, userId);
    }

    @PatchMapping("/doSignIn/{id}")
    public Result doUserSignIn(@PathVariable("id") Long UserSignInId) {
        return userSignInService.doUserSignIn(UserSignInId);

    }

}
