package com.projn.alps.struct;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * http request info
 *
 * @author : sunyuecheng
 */
public class HttpRequestInfo implements Serializable {

    private Locale locale = null;

    private Object paramObj = null;

    private Map<String, Object> extendInfoMap;

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

    /**
     * http request info
     *
     * @param locale        :
     * @param paramObj      :
     * @param extendInfoMap :
     */
    public HttpRequestInfo(Locale locale, Object paramObj, Map<String, Object> extendInfoMap) {
        this.locale = locale;
        this.paramObj = paramObj;
        this.extendInfoMap = extendInfoMap;
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

    public Map<String, Object> getExtendInfoMap() {
        return extendInfoMap;
    }

    public void setExtendInfoMap(Map<String, Object> extendInfoMap) {
        this.extendInfoMap = extendInfoMap;
    }
}
