package com.projn.alps.msg.request;

/**
 * http send msg request info
 *
 * @author : sunyuecheng
 */
public class HttpSendMsgRequestMsgInfo {


    /**
     * agent id
     */
    private String agentId = null;

    /**
     * msg id
     */
    private String msgId;

    /**
     * msg
     */
    private Object msg = null;

    /**
     *
     */
    public HttpSendMsgRequestMsgInfo() {
    }

    /**
     * http send msg request info
     *
     * @param agentId :
     * @param msgId   :
     * @param msg     :
     */
    public HttpSendMsgRequestMsgInfo(String agentId, String msgId, Object msg) {
        this.agentId = agentId;
        this.msgId = msgId;
        this.msg = msg;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
