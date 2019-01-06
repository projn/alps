package com.projn.alps.msg.request;

import java.util.List;

/**
 * http batch send msg request info
 *
 * @author : sunyuecheng
 */
public class HttpBatchSendMsgRequestMsgInfo {

    private List<String> agentIdList = null;

    private int msgId = 0;

    private Object msg = null;

    public List<String> getAgentIdList() {
        return agentIdList;
    }

    public void setAgentIdList(List<String> agentIdList) {
        this.agentIdList = agentIdList;
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
}
