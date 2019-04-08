package com.projn.alps.msg.request;

import java.io.Serializable;

/**
 * web socket request message
 *
 * @author : sunyuecheng
 */
public class WsRequestMsgInfo implements Serializable {

    /**
     * msg id
     */
    private String msgId;

    /**
     * agent id
     */
    private String agentId;

    /**
     * msg
     */
    private Object msg;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
