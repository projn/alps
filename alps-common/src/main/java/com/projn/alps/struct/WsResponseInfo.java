package com.projn.alps.struct;

import java.util.Map;

/**
 * ws response info
 *
 * @author : sunyuecheng
 */
public class WsResponseInfo {

    /**
     * msg
     */
    private Object msg = null;

    private Map<String, Object> extendInfoMap = null;

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Map<String, Object> getExtendInfoMap() {
        return extendInfoMap;
    }

    public void setExtendInfoMap(Map<String, Object> extendInfoMap) {
        this.extendInfoMap = extendInfoMap;
    }
}
