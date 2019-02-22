package com.projn.alps.alpsconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;

/**
 * alps config server application
 *
 * @author : sunyuecheng
 */
@SpringBootApplication
@EnableConfigServer
@ComponentScan("com.projn.alps")
@EnableDiscoveryClient
public class AlpsConfigServerApplication {

    /**
     * main
     *
     * @param args :
     * @throws Exception :
     */
    public static void main(String[] args) {
        SpringApplication.run(AlpsConfigServerApplication.class, args);
    }

}
