package com.projn.alps.aop;

import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;
import org.aspectj.lang.JoinPoint;

/**
 * ws service monitor aop
 *
 * @author : sunyuecheng
 */
public interface IWsServiceMonitorAop {
    /**
     * before handler
     *
     * @param joinPoint       :
     * @param wsRequestInfo :
     */
    void beforeHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo);

    /**
     * after handler
     *
     * @param joinPoint        :
     * @param wsRequestInfo :
     */
    void afterHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo);

    /**
     * return handler
     *
     * @param joinPoint        :
     * @param wsRequestInfo  :
     * @param wsResponseInfo :
     */
    void returnHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo, WsResponseInfo wsResponseInfo);

}
