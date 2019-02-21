package com.projn.alps.alpsmicroservice.handler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * http session handler
 *
 * @author : sunyuecheng
 */
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing=true)
public class HttpSessionHandler extends HttpSessionHandshakeInterceptor {

    /**
     * before handshake
     *
     * @param request    :
     * @param response   :
     * @param wsHandler  :
     * @param attributes :
     * @return boolean :
     * @throws Exception :
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                attributes.put(HttpSession.class.getName(), httpSession);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
