package com.projn.alps.msg.response;

/**
 * web socket response message
 *
 * @author : sunyuecheng
 */
public class WsResponseMsgInfo {

    /**
     * msgid
     */
    private int msgid;

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
     * @param msgid :
     * @param msg   :
     */
    public WsResponseMsgInfo(int msgid, Object msg) {
        this.msgid = msgid;
        this.msg = msg;
    }

    public int getMsgid() {
        return msgid;
    }

    public void setMsgid(int msgid) {
        this.msgid = msgid;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
