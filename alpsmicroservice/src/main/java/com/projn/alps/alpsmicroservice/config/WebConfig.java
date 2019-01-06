package com.projn.alps.alpsmicroservice.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.projn.alps.alpsmicroservice.handler.HttpExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * web config
 *
 * @author : sunyuecheng
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    /**
     * configure handler exception resolvers
     *
     * @param exceptionResolvers :
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        super.configureHandlerExceptionResolvers(exceptionResolvers);
        exceptionResolvers.add(new HttpExceptionHandler());
    }

    /**
     * configure message converters
     *
     * @param converters :
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        converters.add(fastJsonHttpMessageConverter);
    }

}
