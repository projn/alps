package com.projn.alps.alpsmicroservice.aop;

import com.projn.alps.aop.IWsServiceMonitorAop;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * console service monitor aop
 *
 * @author : sunyuecheng
 */
@Aspect
@Component
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing=true)
public class WsServiceMonitorAop {

    private List<IWsServiceMonitorAop> wsServiceMonitorAopList = null;

    public List<IWsServiceMonitorAop> getWsServiceMonitorAopList() {
        return wsServiceMonitorAopList;
    }

    public void setWsServiceMonitorAopList(List<IWsServiceMonitorAop> wsServiceMonitorAopList) {
        this.wsServiceMonitorAopList = wsServiceMonitorAopList;
    }

    /**
     * user operation
     */
    @Pointcut("execution(* com.projn.alps.service.IComponentsWsService.*(..))")
    private void userOperation() {

    }

    /**
     * before handler
     *
     * @param joinPoint       :
     * @param wsRequestInfo :
     */
    @Before(value = "userOperation() && args(wsRequestInfo)")
    public void beforeHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo) {
        if (wsServiceMonitorAopList != null) {
            for (IWsServiceMonitorAop consoleServiceMonitorAop : wsServiceMonitorAopList) {
                consoleServiceMonitorAop.beforeHandler(joinPoint, wsRequestInfo);
            }
        }
    }

    /**
     * after handler
     *
     * @param joinPoint :
     * @param wsRequestInfo :
     */
    @After(value = "userOperation() && args(wsRequestInfo)")
    public void afterHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo) {
        if (wsServiceMonitorAopList != null) {
            for (IWsServiceMonitorAop wsServiceMonitorAop : wsServiceMonitorAopList) {
                wsServiceMonitorAop.afterHandler(joinPoint, wsRequestInfo);
            }
        }
    }

    /**
     * return handler
     *
     * @param joinPoint        :
     * @param wsRequestInfo  :
     * @param wsResponseInfo :
     */
    @AfterReturning(value = "userOperation() && args(wsRequestInfo)", returning = "wsResponseInfo")
    public void returnHandler(JoinPoint joinPoint, WsRequestInfo wsRequestInfo,
                              WsResponseInfo wsResponseInfo) {
        if (wsServiceMonitorAopList != null) {
            for (IWsServiceMonitorAop wsServiceMonitorAop : wsServiceMonitorAopList) {
                wsServiceMonitorAop.returnHandler(joinPoint, wsRequestInfo, wsResponseInfo);
            }
        }
    }
}
