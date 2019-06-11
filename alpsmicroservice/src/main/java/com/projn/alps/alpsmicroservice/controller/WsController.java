package com.projn.alps.alpsmicroservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.work.WsProcessWorker;
import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.exception.HttpException;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.msg.request.WsRequestMsgInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import com.projn.alps.widget.WsSessionInfoMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.AGENT_ID_KEY;
import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.WEBSOCKET_DEFAULT_BUFFER_SIZE;
import static com.projn.alps.define.HttpDefine.HTTP_METHOD_POST;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVALID_REQUEST_INFO_ERROR;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * web socket controller
 *
 * @author : sunyuecheng
 */
@Component
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing = true)
public class WsController extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsController.class);

    @Autowired
    @Qualifier("AgentMasterInfoDao")
    private IAgentMasterInfoDao agentMasterInfoDao;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * after connection established
     *
     * @param session :
     * @throws Exception :
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(WEBSOCKET_DEFAULT_BUFFER_SIZE);
    }

    /**
     * after connection closed
     *
     * @param session :
     * @param status  :
     * @throws Exception :
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        try {
            removeWebSocketSessionInfo(session);
        } catch (Exception e) {
            LOGGER.error("Close web socket connection error, error info({}).", formatExceptionInfo(e));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String textMsg = message.getPayload();
        try {

            WsRequestMsgInfo wsRequestMsgInfo =
                    JSONObject.parseObject(textMsg, WsRequestMsgInfo.class);
            if (wsRequestMsgInfo == null) {
                throw new Exception("Invaild web socket request msg,msg info(" + message + ").");
            }

            RequestServiceInfo requestServiceInfo = getRequestServiceInfo(wsRequestMsgInfo.getMsgId().toString());

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
                        wsRequestInfo, session, agentMasterInfoDao));
            } else {
                LOGGER.debug("System is busy.");
            }
        } catch (Exception e) {
            LOGGER.error("Analyse request info error,msg info({}),error info({}).",
                    message, formatExceptionInfo(e));
            removeWebSocketSessionInfo(session);
        }
    }

    private RequestServiceInfo getRequestServiceInfo(String uri) throws Exception {
        Map<String, List<RequestServiceInfo>> requestServiceInfoMap
                = ServiceData.getRequestServiceInfoMap().get(uri);
        if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()
                || requestServiceInfoMap.get(HTTP_METHOD_POST.toLowerCase()) == null) {
            throw new Exception("Invaild request service info, msg id("
                    + uri + "), method(" + HTTP_METHOD_POST + ").");
        }

        List<RequestServiceInfo> requestServiceInfoList = requestServiceInfoMap.get(HTTP_METHOD_POST.toLowerCase());
        if (requestServiceInfoList.size() != 1) {
            LOGGER.error("Invaild request service info, msg id({}), method({}).",
                    uri, HTTP_METHOD_POST);
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVALID_REQUEST_INFO_ERROR);
        }
        RequestServiceInfo requestServiceInfo = requestServiceInfoList.get(0);

        if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_WS)) {
            throw new Exception("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
        }

        return requestServiceInfo;
    }

    /**
     * handle transport error
     *
     * @param session :
     * @param error   :
     * @throws Exception :
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) throws Exception {
        LOGGER.error("Web socket connection error, error info({}).", formatExceptionInfo(error));
        try {
            removeWebSocketSessionInfo(session);
        } catch (Exception e) {
            LOGGER.error("Close web socket connection error,error info({}).", formatExceptionInfo(error));
        }
    }

    private void removeWebSocketSessionInfo(WebSocketSession session) {
        if (session == null) {
            return;
        }

        try {
            String agentId = (String) session.getAttributes().get(AGENT_ID_KEY);
            if (!StringUtils.isEmpty(agentId)) {
                WsSessionInfoMap.getInstance().removeWebSocketSessionInfo(agentId);
                agentMasterInfoDao.deleteAgentMasterInfo(agentId);
            } else {
                session.close();
            }
        } catch (Exception e) {
            LOGGER.error("Close web socket connection error,error info({}).", formatExceptionInfo(e));
        }
    }
}
