package com.cyj.xx.exception;


/**
 * 用户未登陆异常
 */
public class UserNotLoginException extends RuntimeException {

    public UserNotLoginException() {
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

}
