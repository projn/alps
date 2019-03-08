package com.projn.alps.struct;

import java.io.Serializable;
import java.util.Locale;

/**
 * http request info
 *
 * @author : sunyuecheng
 */
public class HttpRequestInfo implements Serializable {

    private Locale locale = null;

    private Object paramObj = null;

    /**
     * http request info
     */
    public HttpRequestInfo() {
    }

    /**
     * http request info
     *
     * @param paramObj :
     */
    public HttpRequestInfo(Object paramObj) {
        this.paramObj = paramObj;
    }

    /**
     * http request info
     *
     * @param locale   :
     * @param paramObj :
     */
    public HttpRequestInfo(Locale locale, Object paramObj) {
        this.locale = locale;
        this.paramObj = paramObj;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Object getParamObj() {
        return paramObj;
    }

    public void setParamObj(Object paramObj) {
        this.paramObj = paramObj;
    }
}
