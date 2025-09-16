package com.cyj.xx.GlobalExceptionHandler;


import com.cyj.xx.exception.PasswordErrorException;
import com.cyj.xx.exception.UserBannedException;
import com.cyj.xx.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = UserBannedException.class)
    public Result userBannedException(UserBannedException e) {

        return Result.error("账号被封禁中");
    }

    @ExceptionHandler(value = PasswordErrorException.class)
    public Result passwordErrorException(PasswordErrorException e) {

        return Result.error("账号密码错误");
    }
}
