package com.projn.alps.msg.response;

/**
 * http send msg response info
 *
 * @author : sunyuecheng
 */
public class HttpSendMsgResponseMsgInfo {

    /**
     * send status ok
     */
    public static final int SEND_STATUS_OK = 0;

    /**
     * send status agent off line
     */
    public static final int SEND_STATUS_AGENT_OFF_LINE = 1;

    /**
     * send status error
     */
    public static final int SEND_STATUS_ERROR = 2;


    /**
     * agent id
     */
    private String agentId = null;

    /**
     * status
     */
    private Integer status = SEND_STATUS_OK;

    /**
     *
     */
    public HttpSendMsgResponseMsgInfo() {
    }

    /**
     * http send msg response info
     *
     * @param agentId :
     * @param status  :
     */
    public HttpSendMsgResponseMsgInfo(String agentId, Integer status) {
        this.agentId = agentId;
        this.status = status;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
