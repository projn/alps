package com.projn.alps.msg.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * http error response info
 *
 * @author : sunyuecheng
 */
public class HttpErrorResponseMsgInfo {

    /**
     * error code
     */
    @JSONField(name = "error_code")
    private String errorCode = null;

    /**
     * error description
     */
    @JSONField(name = "error_description")
    private String errorDescription = null;

    /**
     *
     */
    public HttpErrorResponseMsgInfo() {
    }

    /**
     * http error response info
     *
     * @param errorCode        :
     * @param errorDescription :
     */
    public HttpErrorResponseMsgInfo(String errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
