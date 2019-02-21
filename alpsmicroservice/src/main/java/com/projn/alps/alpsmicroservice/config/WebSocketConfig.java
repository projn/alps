package com.projn.alps.alpsmicroservice.config;

import com.projn.alps.alpsmicroservice.controller.WsController;
import com.projn.alps.alpsmicroservice.handler.HttpSessionHandler;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * web socket config
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableWebSocket
@EnableConfigurationProperties(RunTimeProperties.class)
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing=true)
public class WebSocketConfig implements WebSocketConfigurer {


    @Autowired
    private RunTimeProperties runTimeProperties;

    /**
     * register web socket handlers
     * @param registry :
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketController(), runTimeProperties.getWebsocketContextPath()).
                addInterceptors(new HttpSessionHandler());
    }

    /**
     * web socket controller
     * @return org.springframework.web.socket.WebSocketHandler :
     */
    @Bean
    public WebSocketHandler webSocketController() {
        return new WsController();
    }
}