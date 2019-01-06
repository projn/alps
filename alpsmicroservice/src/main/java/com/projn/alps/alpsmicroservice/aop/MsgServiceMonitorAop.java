package com.projn.alps.alpsmicroservice.aop;

import com.projn.alps.aop.IMsgServiceMonitorAop;
import com.projn.alps.struct.MsgRequestInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * msg service monitor aop
 *
 * @author : sunyuecheng
 */
@Aspect
@Component
public class MsgServiceMonitorAop {

    private List<IMsgServiceMonitorAop> msgServiceMonitorAopList = null;

    public List<IMsgServiceMonitorAop> getMsgServiceMonitorAopList() {
        return msgServiceMonitorAopList;
    }

    public void setMsgServiceMonitorAopList(List<IMsgServiceMonitorAop> msgServiceMonitorAopList) {
        this.msgServiceMonitorAopList = msgServiceMonitorAopList;
    }

    /**
     * user operation
     */
    @Pointcut("execution(* com.projn.alps.service.IComponentsMsgService.*(..))")
    private void userOperation() {

    }

    /**
     * before handler
     *
     * @param joinPoint       :
     * @param msgRequestInfo :
     */
    @Before(value = "userOperation() && args(msgRequestInfo)")
    public void beforeHandler(JoinPoint joinPoint, MsgRequestInfo msgRequestInfo) {
        if (msgServiceMonitorAopList != null) {
            for (IMsgServiceMonitorAop msgServiceMonitorAop : msgServiceMonitorAopList) {
                msgServiceMonitorAop.beforeHandler(joinPoint, msgRequestInfo);
            }
        }
    }

    /**
     * after handler
     *
     * @param joinPoint :
     * @param msgRequestInfo :
     */
    @After(value = "userOperation() && args(msgRequestInfo)")
    public void afterHandler(JoinPoint joinPoint, MsgRequestInfo msgRequestInfo) {
        if (msgServiceMonitorAopList != null) {
            for (IMsgServiceMonitorAop msgServiceMonitorAop : msgServiceMonitorAopList) {
                msgServiceMonitorAop.afterHandler(joinPoint, msgRequestInfo);
            }
        }
    }
}