package com.projn.alps.struct;

import com.projn.alps.filter.IAuthorizationFilter;

import java.util.List;

/**
 * request service info
 *
 * @author : sunyuecheng
 */
public class RequestServiceInfo {
    public static final String SERVICE_METHOD_HTTP_POST="post";
    public static final String SERVICE_METHOD_HTTP_GET="get";
    public static final String SERVICE_METHOD_HTTP_PUT="put";
    public static final String SERVICE_METHOD_HTTP_DELETE="delete";

    public static final String SERVICE_METHOD_WS_POST="post";

    public static final String SERVICE_METHOD_MSG_NORMAL="normal";
    public static final String SERVICE_METHOD_MSG_ORDER="order";
    public static final String SERVICE_METHOD_MSG_BROADCAST="broadcast";

    public static final String SERVICE_TYPE_HTTP="http";
    public static final String SERVICE_TYPE_WS="ws";
    public static final String SERVICE_TYPE_MSG="msg";

    private String serviceName;

    private String method;

    private String type;

    private List<String> userRoleNameList;

    private Class paramClass;

    private IAuthorizationFilter authorizationFilter = null;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUserRoleNameList() {
        return userRoleNameList;
    }

    public void setUserRoleNameList(List<String> userRoleNameList) {
        this.userRoleNameList = userRoleNameList;
    }

    public Class getParamClass() {
        return paramClass;
    }

    public void setParamClass(Class paramClass) {
        this.paramClass = paramClass;
    }

    public IAuthorizationFilter getAuthorizationFilter() {
        return authorizationFilter;
    }

    public void setAuthorizationFilter(IAuthorizationFilter authorizationFilter) {
        this.authorizationFilter = authorizationFilter;
    }
}