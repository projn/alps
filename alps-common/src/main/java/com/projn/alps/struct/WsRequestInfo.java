package com.projn.alps.struct;

import java.util.Map;

/**
 * ws request info
 *
 * @author : sunyuecheng
 */
public class WsRequestInfo {


    private Object paramObj = null;

    private Map<String, Object> extendInfoMap;

    /**
     * ws request info
     */
    public WsRequestInfo() {
    }

    /**
     * ws request info
     *
     * @param paramObj :
     */
    public WsRequestInfo(Object paramObj) {
        this.paramObj = paramObj;
    }

    /**
     * ws request info
     *
     * @param paramObj      :
     * @param extendInfoMap :
     */
    public WsRequestInfo(Object paramObj, Map<String, Object> extendInfoMap) {
        this.paramObj = paramObj;
        this.extendInfoMap = extendInfoMap;
    }

    public Object getParamObj() {
        return paramObj;
    }

    public void setParamObj(Object paramObj) {
        this.paramObj = paramObj;
    }

    public Map<String, Object> getExtendInfoMap() {
        return extendInfoMap;
    }

    public void setExtendInfoMap(Map<String, Object> extendInfoMap) {
        this.extendInfoMap = extendInfoMap;
    }
}
