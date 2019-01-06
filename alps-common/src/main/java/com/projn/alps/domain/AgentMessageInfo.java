package com.projn.alps.domain;

import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;

/**
 * agent message info
 *
 * @author : sunyuecheng
 */
public class AgentMessageInfo {
    private String agentId;

    private int msgId;

    private Object msg;

    private Long expireTime;

    /**
     * agent message info
     */
    public AgentMessageInfo() {
    }

    /**
     * agent message info
     *
     * @param agentId :
     * @param msgId   :
     * @param msg     :
     */
    public AgentMessageInfo(String agentId, int msgId, Object msg) {
        this.agentId = agentId;
        this.msgId = msgId;
        this.msg = msg;
    }

    /**
     * agent message info
     *
     * @param agentId        :
     * @param msgId          :
     * @param msg            :
     * @param timeoutSeconds :
     */
    public AgentMessageInfo(String agentId, int msgId, Object msg, long timeoutSeconds) {
        this.agentId = agentId;
        this.msgId = msgId;
        this.msg = msg;
        this.expireTime = System.currentTimeMillis() + timeoutSeconds * MILLI_SECOND_1000;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}