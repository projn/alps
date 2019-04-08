package com.projn.alps.service;

import com.projn.alps.struct.RpcRequestInfo;
import com.projn.alps.struct.RpcResponseInfo;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;

/**
 * components rpc service
 *
 * @author : sunyuecheng
 */
public interface IComponentsRpcService {
    /**
     * execute
     *
     * @param rpcRequestInfo :
     * @return RpcResponseInfo :
     */
    RpcResponseInfo execute(RpcRequestInfo rpcRequestInfo);
}
