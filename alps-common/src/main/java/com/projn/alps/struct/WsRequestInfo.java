package com.projn.alps.struct;

/**
 * ws request info
 *
 * @author : sunyuecheng
 */
public class WsRequestInfo {


    private Object paramObj = null;

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

    public Object getParamObj() {
        return paramObj;
    }

    public void setParamObj(Object paramObj) {
        this.paramObj = paramObj;
    }

}
