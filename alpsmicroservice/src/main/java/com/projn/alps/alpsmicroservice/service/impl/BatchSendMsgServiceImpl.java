package com.projn.alps.alpsmicroservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.widget.WsSessionInfoMap;
import com.projn.alps.exception.HttpException;
import com.projn.alps.msg.request.HttpBatchSendMsgRequestMsgInfo;
import com.projn.alps.msg.response.HttpBatchSendMsgResponseMsgInfo;
import com.projn.alps.msg.response.HttpSendMsgResponseMsgInfo;
import com.projn.alps.msg.response.WsResponseMsgInfo;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.alps.util.ParamCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

import static com.projn.alps.exception.code.CommonErrorCode.RESULT_ANALYSE_REQUEST_ERROR;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVAILD_PARAM_ERROR;


/**
 * batch send msg service impl
 *
 * @author : sunyuecheng
 */
@Service
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing = true)
public class BatchSendMsgServiceImpl implements IComponentsHttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSendMsgServiceImpl.class);

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

        HttpBatchSendMsgRequestMsgInfo httpBatchSendMsgRequestMsgInfo = null;
        try {
            httpBatchSendMsgRequestMsgInfo = JSON.parseObject(requestJson.toJSONString(),
                    HttpBatchSendMsgRequestMsgInfo.class);
        } catch (Exception e) {
            LOGGER.error("Analyse request error,error info({}).", e.getMessage());
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_ANALYSE_REQUEST_ERROR);
        }

        if (httpBatchSendMsgRequestMsgInfo != null && httpBatchSendMsgRequestMsgInfo.getAgentIdList() != null) {
            try {
                ParamCheckUtils.checkParam(httpBatchSendMsgRequestMsgInfo);
            } catch (Exception e) {
                LOGGER.error("Check param error,error info({}).", e.getMessage());
                throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
            }
        } else {
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
        }

        HttpBatchSendMsgResponseMsgInfo httpBatchSendMsgResponseMsgInfo = new HttpBatchSendMsgResponseMsgInfo();
        List<HttpSendMsgResponseMsgInfo> httpSendMsgResponseMsgInfoList = new ArrayList<>();

        for (int i = 0; i < httpBatchSendMsgRequestMsgInfo.getAgentIdList().size(); i++) {
            String agentId = httpBatchSendMsgRequestMsgInfo.getAgentIdList().get(i);
            HttpSendMsgResponseMsgInfo httpSendMsgResponseMsgInfo = null;

            if (StringUtils.isEmpty(agentId)) {
                httpSendMsgResponseMsgInfo = new HttpSendMsgResponseMsgInfo();
                httpSendMsgResponseMsgInfo.setAgentId(agentId);

                WebSocketSession session =
                        WsSessionInfoMap.getInstance().getWebSocketSession(agentId);
                if (session != null && session.isOpen()) {

                    WsResponseMsgInfo wsResponseMsgInfo =
                            new WsResponseMsgInfo(httpBatchSendMsgRequestMsgInfo.getMsgId(),
                                    httpBatchSendMsgRequestMsgInfo.getMsg());

                    String msg = JSON.toJSONString(wsResponseMsgInfo);
                    try {
                        TextMessage message = new TextMessage(msg);
                        session.sendMessage(message);
                        httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_OK);
                    } catch (Exception e) {
                        LOGGER.error("Send msg to agent error,agent id({}), msg({}).", agentId, msg);
                        httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_ERROR);
                    }
                } else {
                    httpSendMsgResponseMsgInfo.setStatus(HttpSendMsgResponseMsgInfo.SEND_STATUS_AGENT_OFF_LINE);
                }
            } else {
                LOGGER.error("Invaild msg info,agent id list({}), msg({}).",
                        httpBatchSendMsgRequestMsgInfo.getAgentIdList().toString(),
                        httpBatchSendMsgRequestMsgInfo.getMsg());
                throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVAILD_PARAM_ERROR);
            }
            httpSendMsgResponseMsgInfoList.add(httpSendMsgResponseMsgInfo);
        }
        httpBatchSendMsgResponseMsgInfo.setHttpSendMsgResponseMsgInfoList(httpSendMsgResponseMsgInfoList);

        httpResponseInfo.setMsg(httpBatchSendMsgResponseMsgInfo);

        return httpResponseInfo;
    }
}
