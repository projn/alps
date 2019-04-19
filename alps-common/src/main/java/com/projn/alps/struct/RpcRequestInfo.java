package com.projn.alps.struct;

import java.util.Map;

/**
 * rpc request info
 *
 * @author : sunyuecheng
 */
public class RpcRequestInfo {


    private Object paramObj = null;

    private Map<String, Object> extendInfoMap = null;

    /**
     * rpc request info
     */
    public RpcRequestInfo() {
    }

    /**
     * rpc request info
     *
     * @param paramObj :
     */
    public RpcRequestInfo(Object paramObj) {
        this.paramObj = paramObj;
    }


    /**
     * rpc request info
     *
     * @param paramObj :
     * @param extendInfoMap :
     */
    public RpcRequestInfo(Object paramObj, Map<String, Object> extendInfoMap) {
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
