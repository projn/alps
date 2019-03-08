package com.projn.alps.alpsmicroservice;

import com.projn.alps.alpsmicroservice.listener.SystemInitializeContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * alps micro service application
 *
 * @author : sunyuecheng
 */
@Configuration
@ComponentScan("com")
@ServletComponentScan
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableDiscoveryClient
public class AlpsMicroServiceApplication {

    private static final int RUN_PARAM_SIZE = 2;
    private static final String RUN_PARAM_SPRING_CONTEXT_HEADER = "--spring.config.location";

    /**
     * main
     *
     * @param args :
     * @throws Exception :
     */
    public static void main(String[] args) throws Exception {
        if (args.length != RUN_PARAM_SIZE || !args[0].startsWith(RUN_PARAM_SPRING_CONTEXT_HEADER)) {
            throw new Exception("Invaild run param.");
        }
        SpringApplication springApplication = new SpringApplication(AlpsMicroServiceApplication.class);
        springApplication.addListeners(new SystemInitializeContextListener(args[1]));
        springApplication.run(args);
    }
}
