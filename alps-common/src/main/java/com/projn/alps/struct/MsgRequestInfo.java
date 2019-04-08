package com.projn.alps.struct;

import java.util.Map;

/**
 * msg request info
 *
 * @author : sunyuecheng
 */
public class MsgRequestInfo {

    private String id;

    private Object msg;

    private Map<String, Object> extendInfoMap = null;

    /**
     * msg request info
     */
    public MsgRequestInfo() {
    }

    /**
     * msg request info
     *
     * @param id            :
     * @param msg           :
     * @param extendInfoMap :
     */
    public MsgRequestInfo(String id, Object msg, Map<String, Object> extendInfoMap) {
        this.id = id;
        this.msg = msg;
        this.extendInfoMap = extendInfoMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getExtendInfoMap() {
        return extendInfoMap;
    }

    public void setExtendInfoMap(Map<String, Object> extendInfoMap) {
        this.extendInfoMap = extendInfoMap;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
