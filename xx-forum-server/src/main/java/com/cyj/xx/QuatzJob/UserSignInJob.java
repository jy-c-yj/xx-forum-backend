package com.cyj.xx.QuatzJob;

import com.cyj.xx.service.Impl.UserServiceImpl;
import com.cyj.xx.service.UserSignInService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;


@Slf4j
public class UserSignInJob extends QuartzJobBean {

    @Autowired
    UserSignInService userSignInService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("更新签到状态-----{}", LocalDateTime.now());
        userSignInService.BatchSignInStatus();
    }
}
