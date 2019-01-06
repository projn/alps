package com.projn.alps.struct;

import org.springframework.web.socket.WebSocketSession;

/**
 * web socket session info
 *
 * @author : sunyuecheng
 */
public class WebSocketSessionInfo {
    private WebSocketSession session = null;
    private Long createTime = null;
    private Long lastReceiveDataTime = null;
    private Long lastSendDataTime = null;

    /**
     * web socket session info
     *
     * @param session :
     */
    public WebSocketSessionInfo(WebSocketSession session) {

        this.session = session;
        this.createTime = System.currentTimeMillis();
        this.lastReceiveDataTime = this.createTime;
        this.lastSendDataTime = System.currentTimeMillis();
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public Long getLastReceiveDataTime() {
        return lastReceiveDataTime;
    }

    public void setLastReceiveDataTime(Long lastReceiveDataTime) {
        this.lastReceiveDataTime = lastReceiveDataTime;
    }

    public Long getLastSendDataTime() {
        return lastSendDataTime;
    }

    public void setLastSendDataTime(Long lastSendDataTime) {
        this.lastSendDataTime = lastSendDataTime;
    }
}
