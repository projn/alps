package com.projn.alps.exception;

import com.projn.alps.exception.code.IErrorCode;

/**
 * http exception
 *
 * @author : sunyuecheng
 */
public class HttpException extends Exception {

    private int httpStatus;

    private String errorCode;

    private String errorDescription;

    /**
     * http exception
     *
     * @param httpStatus :
     * @param errorCode  :
     */
    public HttpException(int httpStatus, IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode.getErrorCode();
        this.errorDescription = errorCode.getMessage();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
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
