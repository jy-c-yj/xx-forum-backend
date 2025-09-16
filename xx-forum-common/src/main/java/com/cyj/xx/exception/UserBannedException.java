package com.cyj.xx.exception;

/**
 * 账号被封禁异常
 */
public class UserBannedException extends RuntimeException {

    public UserBannedException() {
    }

    public UserBannedException(String msg) {
        super(msg);
    }

}
