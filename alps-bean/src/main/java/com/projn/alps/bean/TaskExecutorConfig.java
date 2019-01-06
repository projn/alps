package com.projn.alps.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * task executor config
 *
 * @author : sunyuecheng
 */
@Configuration
@PropertySource("file:${config.dir}/config/threadpool.properties")
public class TaskExecutorConfig {

    @Value("${threadpool.corePoolSize}")
    private Integer corePoolSize = 1;

    @Value("${threadpool.maxPoolSize}")
    private Integer maxPoolSize = Integer.MAX_VALUE;

    @Value("${threadpool.keepAliveSeconds}")
    private Integer keepAliveSeconds = 0;

    @Value("${threadpool.queueCapacity}")
    private Integer queueCapacity = Integer.MAX_VALUE;

    /**
     * thread pool task executor
     * @return org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor :
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());

        return threadPoolTaskExecutor;
    }
}
