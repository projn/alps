package com.projn.alps.msg.response;

/**
 * web socket response message
 *
 * @author : sunyuecheng
 */
public class WsResponseMsgInfo {

    /**
     * msgId
     */
    private String msgId;

    /**
     * msg
     */
    private Object msg;

    /**
     *
     */
    public WsResponseMsgInfo() {
    }

    /**
     * web socket response message
     *
     * @param msgId :
     * @param msg   :
     */
    public WsResponseMsgInfo(String msgId, Object msg) {
        this.msgId = msgId;
        this.msg = msg;
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
