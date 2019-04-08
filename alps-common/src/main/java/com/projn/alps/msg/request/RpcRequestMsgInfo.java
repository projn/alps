package com.projn.alps.msg.request;

import java.io.Serializable;

/**
 * rpc request message
 *
 * @author : sunyuecheng
 */
public class RpcRequestMsgInfo implements Serializable {

    /**
     * service name
     */
    private String serviceName;

    /**
     * request body
     */
    private String requestBody;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
