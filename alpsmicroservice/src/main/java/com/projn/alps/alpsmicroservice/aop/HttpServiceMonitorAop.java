package com.projn.alps.alpsmicroservice.aop;

import com.projn.alps.aop.IHttpServiceMonitorAop;
import com.projn.alps.exception.HttpException;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * http service monitor aop
 *
 * @author : sunyuecheng
 */
@Aspect
@Component
public class HttpServiceMonitorAop {

    private List<IHttpServiceMonitorAop> httpServiceMonitorAopList = null;

    public List<IHttpServiceMonitorAop> getHttpServiceMonitorAopList() {
        return httpServiceMonitorAopList;
    }

    public void setHttpServiceMonitorAopList(List<IHttpServiceMonitorAop> httpServiceMonitorAopList) {
        this.httpServiceMonitorAopList = httpServiceMonitorAopList;
    }

    /**
     * user operation
     */
    @Pointcut("execution(* com.projn.alps.service.IComponentsHttpService.*(..))")
    public void userOperation() {

    }

    /**
     * before handler
     *
     * @param joinPoint       :
     * @param httpRequestInfo :
     */
    @Before(value = "userOperation() && args(httpRequestInfo)")
    public void beforeHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo) {
        if (httpServiceMonitorAopList != null) {
            for (IHttpServiceMonitorAop httpServiceMonitorAop : httpServiceMonitorAopList) {
                httpServiceMonitorAop.beforeHandler(joinPoint, httpRequestInfo);
            }
        }
    }

    /**
     * after handler
     *
     * @param joinPoint :
     */
    @After(value = "userOperation()")
    public void afterHandler(JoinPoint joinPoint) {
        if (httpServiceMonitorAopList != null) {
            for (IHttpServiceMonitorAop httpServiceMonitorAop : httpServiceMonitorAopList) {
                httpServiceMonitorAop.afterHandler(joinPoint, null);
            }
        }
    }

    /**
     * return handler
     *
     * @param joinPoint        :
     * @param httpRequestInfo  :
     * @param httpResponseInfo :
     */
    @AfterReturning(value = "userOperation() && args(httpRequestInfo)", returning = "httpResponseInfo")
    public void returnHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo,
                              HttpResponseInfo httpResponseInfo) {
        if (httpServiceMonitorAopList != null) {
            for (IHttpServiceMonitorAop httpServiceMonitorAop : httpServiceMonitorAopList) {
                httpServiceMonitorAop.returnHandler(joinPoint, httpRequestInfo, httpResponseInfo);
            }
        }
    }

    /**
     * throw exception handler
     *
     * @param joinPoint       :
     * @param httpRequestInfo :
     * @param httpException   :
     */
    @AfterThrowing(value = "userOperation() && args(httpRequestInfo)", throwing = "httpException")
    public void throwExceptionHandler(JoinPoint joinPoint, HttpRequestInfo httpRequestInfo,
                                      HttpException httpException) {
        if (httpServiceMonitorAopList != null) {
            for (IHttpServiceMonitorAop httpServiceMonitorAop : httpServiceMonitorAopList) {
                httpServiceMonitorAop.throwExceptionHandler(joinPoint, httpRequestInfo, httpException);
            }
        }
    }
}
