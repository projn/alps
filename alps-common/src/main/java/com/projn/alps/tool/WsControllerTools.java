package com.projn.alps.tool;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.exception.HttpException;
import com.projn.alps.i18n.LocaleContext;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.msg.request.WsRequestMsgInfo;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import com.projn.alps.work.HttpProcessWorker;
import com.projn.alps.work.WsProcessWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.MAX_HTTP_RESPONSE_WAIT_SECONDS;
import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;
import static com.projn.alps.define.HttpDefine.HTTP_METHOD_POST;
import static com.projn.alps.exception.code.CommonErrorCode.*;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * http controller tools
 *
 * @author : sunyuecheng
 */
@Component
public class WsControllerTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsControllerTools.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * deal
     *
     * @param session          :
     * @param message      :
     * @return boolean :
     */
    public boolean deal(WebSocketSession session, TextMessage message) {
        if (session == null || message == null) {
            LOGGER.error("Invaild param.");
            return false;
        }

        if (message.getPayload() == null) {
            return false;
        }
        String textMsg = message.getPayload();
        try {

            WsRequestMsgInfo wsRequestMsgInfo =
                    JSONObject.parseObject(textMsg, WsRequestMsgInfo.class);
            if (wsRequestMsgInfo == null) {
                throw new Exception("Invaild web socket request msg,msg info(" + message + ").");
            }

            Map<String, RequestServiceInfo> requestServiceInfoMap
                    = ServiceData.getRequestServiceInfoMap().get(wsRequestMsgInfo.getMsgId().toString());
            if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()
                    || requestServiceInfoMap.get(HTTP_METHOD_POST.toLowerCase()) == null) {
                throw new Exception("Invaild request service info, msg id("
                        + wsRequestMsgInfo.getMsgId() + "), method(" + HTTP_METHOD_POST + ").");
            }
            RequestServiceInfo requestServiceInfo
                    = requestServiceInfoMap.get(HTTP_METHOD_POST.toLowerCase());
            if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_WS)) {
                throw new Exception("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
            }

            if (requestServiceInfo.getAuthorizationFilter() != null) {
                requestServiceInfo.getAuthorizationFilter().auth(session, requestServiceInfo.getUserRoleNameList());
            }

            WsRequestInfo wsRequestInfo = null;
            if (requestServiceInfo.getParamClass() != null) {

                try {
                    wsRequestInfo = RequestInfoUtils.convertWsRequestInfo(session, textMsg,
                            requestServiceInfo.getParamClass());
                } catch (Exception e) {
                    throw new Exception("Convert request info error,error info(" + e.getMessage() + ").");
                }

                if (wsRequestInfo != null && wsRequestInfo.getParamObj() != null) {
                    try {
                        ParamCheckUtils.checkParam(wsRequestInfo.getParamObj());
                    } catch (Exception e) {
                        throw new Exception("Check param error,error info(" + e.getMessage() + ").");
                    }
                }
            }

            if (taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
                taskExecutor.execute(new WsProcessWorker(requestServiceInfo.getServiceName(),
                        wsRequestInfo, session));
            } else {
                LOGGER.debug("System is busy.");
            }
        } catch (Exception e) {
            LOGGER.error("Analyse request info error,msg info({}),error info({}).",
                    message, formatExceptionInfo(e));
            return false;
        }

        return true;
    }

}
