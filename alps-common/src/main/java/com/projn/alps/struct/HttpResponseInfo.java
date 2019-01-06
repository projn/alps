package com.projn.alps.struct;

import java.io.Serializable;
import java.util.Map;

/**
 * http response info
 *
 * @author : sunyuecheng
 */
public class HttpResponseInfo implements Serializable {

    /**
     * msg
     */
    private Object msg = null;

    private Map<String, String> headerInfoMap;

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Map<String, String> getHeaderInfoMap() {
        return headerInfoMap;
    }

    public void setHeaderInfoMap(Map<String, String> headerInfoMap) {
        this.headerInfoMap = headerInfoMap;
    }
}
