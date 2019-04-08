package com.projn.alps.struct;

/**
 * ws request info
 *
 * @author : sunyuecheng
 */
public class RpcRequestInfo {


    private Object paramObj = null;

    /**
     * ws request info
     */
    public RpcRequestInfo() {
    }

    /**
     * ws request info
     *
     * @param paramObj :
     */
    public RpcRequestInfo(Object paramObj) {
        this.paramObj = paramObj;
    }

    public Object getParamObj() {
        return paramObj;
    }

    public void setParamObj(Object paramObj) {
        this.paramObj = paramObj;
    }

}
