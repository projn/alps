package com.projn.alps.aop;

import com.projn.alps.struct.MsgRequestInfo;
import org.aspectj.lang.JoinPoint;

/**
 * msg service monitor aop
 *
 * @author : sunyuecheng
 */
public interface IMsgServiceMonitorAop {
    /**
     * before handler
     *
     * @param joinPoint               :
     * @param msgRequestInfo :
     */
    void beforeHandler(JoinPoint joinPoint, MsgRequestInfo msgRequestInfo);

    /**
     * after handler
     *
     * @param joinPoint               :
     * @param msgRequestInfo :
     */
    void afterHandler(JoinPoint joinPoint, MsgRequestInfo msgRequestInfo);
}
