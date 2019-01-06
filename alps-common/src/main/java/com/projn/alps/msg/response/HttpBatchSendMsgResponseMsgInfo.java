package com.projn.alps.msg.response;

import java.util.List;

/**
 * http batch send msg response info
 *
 * @author : sunyuecheng
 */
public class HttpBatchSendMsgResponseMsgInfo {

    private List<HttpSendMsgResponseMsgInfo> httpSendMsgResponseMsgInfoList = null;

    public List<HttpSendMsgResponseMsgInfo> getHttpSendMsgResponseMsgInfoList() {
        return httpSendMsgResponseMsgInfoList;
    }

    public void setHttpSendMsgResponseMsgInfoList(List<HttpSendMsgResponseMsgInfo> httpSendMsgResponseMsgInfoList) {
        this.httpSendMsgResponseMsgInfoList = httpSendMsgResponseMsgInfoList;
    }
}
