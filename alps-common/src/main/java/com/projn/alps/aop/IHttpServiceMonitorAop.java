package com.projn.alps.aop;

import com.projn.alps.exception.HttpException;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import org.aspectj.lang.JoinPoint;

/**
 * http service monitor aop
 *
 * @author : sunyuecheng
 */
public interface IHttpServiceMonitorAop {
    /**
     * before handler
     *
     * @param joinPoint       :
     * @param httpRequestInfo :
     */
    void beforeHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo);

    /**
     * after handler
     *
     * @param joinPoint        :
     * @param httpResponseInfo :
     */
    void afterHandler(JoinPoint joinPoint, HttpResponseInfo httpResponseInfo);

    /**
     * return handler
     *
     * @param joinPoint        :
     * @param httpRequestInfo  :
     * @param httpResponseInfo :
     */
    void returnHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo, HttpResponseInfo httpResponseInfo);

    /**
     * throw exception handler
     *
     * @param joinPoint       :
     * @param httpRequestInfo :
     * @param httpException   :
     */
    void throwExceptionHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo, HttpException httpException);
}
