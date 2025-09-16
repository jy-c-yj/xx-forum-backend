package com.cyj.xx.exception;

/**
 * 密码修改失败异常
 */
public class PasswordEditFailedException extends RuntimeException {

    public PasswordEditFailedException(String msg){
        super(msg);
    }

}
