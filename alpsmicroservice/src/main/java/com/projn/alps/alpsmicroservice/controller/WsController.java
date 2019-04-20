package com.projn.alps.alpsmicroservice.controller;

import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.tool.WsControllerTools;
import com.projn.alps.widget.WsSessionInfoMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.WEBSOCKET_DEFAULT_BUFFER_SIZE;
import static com.projn.alps.define.CommonDefine.AGENT_ID_KEY;
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
    private WsControllerTools wsControllerTools;

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
        if(!wsControllerTools.deal(session, message)) {
            removeWebSocketSessionInfo(session);
        }
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