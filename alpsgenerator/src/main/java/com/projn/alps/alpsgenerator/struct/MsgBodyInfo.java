package com.projn.alps.alpsgenerator.struct;

import java.util.Map;

/**
 * msg body info
 *
 * @author : sunyuecheng
 */
public class MsgBodyInfo {

    private String name;
    private Map<String, Object> msgBodyMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getMsgBodyMap() {
        return msgBodyMap;
    }

    public void setMsgBodyMap(Map<String, Object> msgBodyMap) {
        this.msgBodyMap = msgBodyMap;
    }
}
