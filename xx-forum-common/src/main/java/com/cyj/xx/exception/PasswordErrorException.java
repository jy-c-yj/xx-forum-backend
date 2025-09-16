package com.cyj.xx.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends RuntimeException {

    public PasswordErrorException() {
    }

    public PasswordErrorException(String msg) {
        super(msg);
    }

}
