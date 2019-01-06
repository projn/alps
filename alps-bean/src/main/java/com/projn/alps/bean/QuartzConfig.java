package com.projn.alps.bean;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * quartz config
 *
 * @author : sunyuecheng
 */
@Configuration
public class QuartzConfig {

    /**
     * job factory
     *
     * @return com.projn.alps.module.manager.JobFactory :
     */
    @Bean
    public JobFactory jobFactory() {
        return new JobFactory();
    }

    /**
     * scheduler
     *
     * @return org.quartz.Scheduler :
     * @throws Exception :
     */
    @Bean
    public Scheduler scheduler() throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory());
        schedulerFactoryBean.afterPropertiesSet();
        return schedulerFactoryBean.getScheduler();
    }
}
