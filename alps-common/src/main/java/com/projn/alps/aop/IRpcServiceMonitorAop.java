package com.projn.alps.aop;

import com.projn.alps.struct.RpcRequestInfo;
import com.projn.alps.struct.RpcResponseInfo;
import org.aspectj.lang.JoinPoint;

/**
 * rpc service monitor aop
 *
 * @author : sunyuecheng
 */
public interface IRpcServiceMonitorAop {
    /**
     * before handler
     *
     * @param joinPoint      :
     * @param rpcRequestInfo :
     */
    void beforeHandler(JoinPoint joinPoint, RpcRequestInfo rpcRequestInfo);

    /**
     * after handler
     *
     * @param joinPoint      :
     * @param rpcRequestInfo :
     */
    void afterHandler(JoinPoint joinPoint, RpcRequestInfo rpcRequestInfo);

    /**
     * return handler
     *
     * @param joinPoint       :
     * @param rpcRequestInfo  :
     * @param rpcResponseInfo :
     */
    void returnHandler(JoinPoint joinPoint, RpcRequestInfo rpcRequestInfo, RpcResponseInfo rpcResponseInfo);

}
