package com.projn.alps.alpsmicroservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.widget.WsSessionInfoMap;
import com.projn.alps.exception.HttpException;
import com.projn.alps.msg.response.WsResponseMsgInfo;
import com.projn.alps.msg.request.HttpSendMsgRequestMsgInfo;
import com.projn.alps.msg.response.HttpSendMsgResponseMsgInfo;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.alps.util.ParamCheckUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static com.projn.alps.exception.code.CommonErrorCode.RESULT_ANALYSE_REQUEST_ERROR;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVAILD_PARAM_ERROR;

/**
 * send msg service impl
 *
 * @author : sunyuecheng
 */
@Service
public class SendMsgServiceImpl implements IComponentsHttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMsgServiceImpl.class);

    /**
     * execute
     *
     * @param httpRequestInfo :
     * @return com.projn.alps.struct.HttpResponseInfo :
     * @throws HttpException :
     */
    @Override
    public HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException {
        HttpResponseInfo httpResponseInfo = new HttpResponseInfo();

        if (httpRequestInfo == null || httpRequestInfo.getParamObj() == null) {
            LOGGER.error("Error param.");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
        }
        JSONObject requestJson = (JSONObject) httpRequestInfo.getParamObj();

        HttpSendMsgRequestMsgInfo httpSendMsgRequestMsgInfo = null;
        try {
            httpSendMsgRequestMsgInfo = JSON.parseObject(requestJson.toJSONString(), HttpSendMsgRequestMsgInfo.class);
        } catch (Exception e) {
            LOGGER.error("Analyse request error,error info({}).", e.getMessage());
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_ANALYSE_REQUEST_ERROR);
        }

        if (httpSendMsgRequestMsgInfo != null) {
            try {
                ParamCheckUtils.checkParam(httpSendMsgRequestMsgInfo);
            } catch (Exception e) {
                LOGGER.error("Check param error,error info({}).", e.getMessage());
                throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
            }
        } else {
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
        }

        HttpSendMsgResponseMsgInfo httpSendMsgResponseMsgInfo = new HttpSendMsgResponseMsgInfo();

        if (!StringUtils.isEmpty(httpSendMsgRequestMsgInfo.getAgentId())
                && httpSendMsgRequestMsgInfo.getMsg() == null) {
            httpSendMsgResponseMsgInfo.setAgentId(httpSendMsgRequestMsgInfo.getAgentId());

            WebSocketSession session =
                    WsSessionInfoMap.getInstance().getWebSocketSession(httpSendMsgRequestMsgInfo.getAgentId());
            if (session != null && session.isOpen()) {

                WsResponseMsgInfo wsResponseMsgInfo =
                        new WsResponseMsgInfo(httpSendMsgRequestMsgInfo.getMsgId(),
                                httpSendMsgRequestMsgInfo.getMsg());

                String msg = JSON.toJSONString(wsResponseMsgInfo);
                try {
                    TextMessage message = new TextMessage(msg);
                    session.sendMessage(message);
                    httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_OK);
                } catch (Exception e) {
                    LOGGER.error("Send msg to agent error,agent id({}), msg({}).",
                            httpSendMsgRequestMsgInfo.getAgentId(), msg);
                    httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_ERROR);
                }
            } else {
                httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_AGENT_OFF_LINE);
            }
        } else {
            LOGGER.error("Invaild msg info,agent id({}), msg({}).", httpSendMsgRequestMsgInfo.getAgentId(),
                    httpSendMsgRequestMsgInfo.getMsg());
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
        }

        httpResponseInfo.setMsg(httpSendMsgResponseMsgInfo);

        return httpResponseInfo;
    }
}
