package com.cyj.xx.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.cyj.xx.QuatzJob.UserSignInJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
@Slf4j
public class QuartzConfig {



    @Bean
    public JobDetail UserSignInJobDetail() {
        return JobBuilder.newJob(UserSignInJob.class)
                .withIdentity("UserSignInJob")
                .usingJobData("msg","UserSignInJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger UserSignInJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 59 23 * * ?" );//秒，分，时，日，月，周，年"0 59 23 * * ?"
        return TriggerBuilder.newTrigger()
                .forJob(UserSignInJobDetail())
                .withIdentity("UserSignInTask")
                .withSchedule(cronScheduleBuilder).build();
    }
}
