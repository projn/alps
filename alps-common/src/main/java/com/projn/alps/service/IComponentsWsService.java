package com.projn.alps.service;

import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;

/**
 * components agent ws service
 *
 * @author : sunyuecheng
 */
public interface IComponentsWsService {
    /**
     * execute
     *
     * @param wsRequestInfo :
     * @return WsResponseInfo :
     */
    WsResponseInfo execute(WsRequestInfo wsRequestInfo);
}
