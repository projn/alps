package com.projn.alps.struct;

import java.util.Map;

/**
 * ws request info
 *
 * @author : sunyuecheng
 */
public class MsgRequestInfo {

    private int id;

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
    public MsgRequestInfo(int id, Object msg, Map<String, Object> extendInfoMap) {
        this.id = id;
        this.msg = msg;
        this.extendInfoMap = extendInfoMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
