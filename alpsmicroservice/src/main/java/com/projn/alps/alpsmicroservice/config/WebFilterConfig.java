package com.projn.alps.alpsmicroservice.config;

import com.projn.alps.alpsmicroservice.filter.ActuatorRequestAuthFilter;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * web filter config
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableConfigurationProperties(RunTimeProperties.class)
@ConditionalOnProperty(name = "bean.switch.actuator.auth", havingValue = "true", matchIfMissing=true)
public class WebFilterConfig {

    @Autowired
    private RunTimeProperties runTimeProperties;

    @Autowired
    private ActuatorRequestAuthFilter actuatorRequestAuthFilter;

    /**
     * jwt authentication token filter
     *
     * @param :
     * @return FilterRegistrationBean :
     */
    @Bean
    public FilterRegistrationBean jwtAuthenticationTokenFilter() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName(actuatorRequestAuthFilter.getClass().getName());
        registration.setFilter(actuatorRequestAuthFilter);
        registration.setAsyncSupported(true);
        //registration.setOrder(6);
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
        registration.addUrlPatterns(runTimeProperties.getActuatorContextPath() + "/*");
        return registration;
    }

}
