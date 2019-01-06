package com.projn.alps.alpsmicroservice;

import com.projn.alps.alpsmicroservice.listener.SystemInitializeContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * alps micro service application
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableWebMvc
@ComponentScan("com")
@ServletComponentScan
@SpringBootApplication
@EnableAspectJAutoProxy
public class AlpsMicroServiceApplication {

    /**
	 * main
	 *
	 * @param args :
     * @throws Exception :
	 */
	public static void main(String[] args) throws Exception {
	    if(args.length!=1) {
            throw new Exception("Invaild run param.");
        }
		SpringApplication springApplication = new SpringApplication(AlpsMicroServiceApplication.class);
		springApplication.addListeners(new SystemInitializeContextListener(args[0]));
		springApplication.run(args);
	}
}
