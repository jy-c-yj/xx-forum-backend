package com.cyj.xx.Aop;


import com.cyj.xx.entity.UserSignIn;
import com.cyj.xx.mapper.UserSignInMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GetExpAop {


    @Autowired
    UserSignInMapper userSignInMapper;

    @Pointcut("@annotation(com.cyj.xx.Aop.annotation.AddExp)")
    public void addExp() {}



    @After("addExp()")
    public void addExpAop(JoinPoint joinPoint) {
        log.info("执行方法 》》》 {}",joinPoint.getSignature().getName());
        log.info("方法参数 》》》 {}",joinPoint.getArgs());
        Long id = (Long) joinPoint.getArgs()[0];
        UserSignIn usi = userSignInMapper.selectUserSignIn(id);
        log.info("当前经验 》》》 {}",usi.getExperience());
        usi.setExperience(usi.getExperience()+5);
        userSignInMapper.UpdateUserSignIn(usi);
        log.info("签到成功，经验+5 》》》 {}",usi.getExperience());

    }
}
