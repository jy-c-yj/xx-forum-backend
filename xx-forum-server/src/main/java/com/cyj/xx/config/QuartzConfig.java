package com.cyj.xx.config;


import com.cyj.xx.QuatzJob.UserSignInJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
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
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 59 23 * * ?");//秒，分，时，日，月，周，年
        return TriggerBuilder.newTrigger()
                .forJob(UserSignInJobDetail())
                .withIdentity("UserSignInTask")
                .withSchedule(cronScheduleBuilder).build();
    }
}
