package com.projn.alps.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.exception.HttpException;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.msg.request.WsRequestMsgInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import com.projn.alps.work.WsProcessWorker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.MSG_ID_KEY;
import static com.projn.alps.define.HttpDefine.HTTP_METHOD_POST;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVALID_REQUEST_INFO_ERROR;
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

    @Autowired
    @Qualifier("AgentMasterInfoDao")
    private IAgentMasterInfoDao agentMasterInfoDao;

    /**
     * deal
     *
     * @param session :
     * @param message :
     * @return boolean :
     */
    public boolean deal(WebSocketSession session, TextMessage message) {
        if (session == null || message == null || StringUtils.isEmpty(message.getPayload())) {
            LOGGER.error("Invaild param.");
            return false;
        }

        String textMsg = message.getPayload();
        try {
            WsRequestMsgInfo wsRequestMsgInfo =
                    JSONObject.parseObject(textMsg, WsRequestMsgInfo.class);
            if (wsRequestMsgInfo == null) {
                throw new Exception("Invaild web socket request msg,msg info(" + message.toString() + ").");
            }

            RequestServiceInfo requestServiceInfo = getRequestServiceInfo(wsRequestMsgInfo.getMsgId());

            if (requestServiceInfo.getAuthorizationFilter() != null) {
                requestServiceInfo.getAuthorizationFilter().auth(session, requestServiceInfo.getUserRoleNameList());
            }

            WsRequestInfo wsRequestInfo = null;
            if (requestServiceInfo.getParamClass() != null) {
                String msgBodyText = JSON.toJSONString(wsRequestMsgInfo.getMsg());
                wsRequestInfo = RequestInfoUtils.convertWsRequestInfo(session, msgBodyText,
                        requestServiceInfo.getParamClass());
                wsRequestInfo.getExtendInfoMap().put(MSG_ID_KEY, wsRequestMsgInfo.getMsgId());

                if (wsRequestInfo != null && wsRequestInfo.getParamObj() != null) {
                    ParamCheckUtils.checkParam(wsRequestInfo.getParamObj());
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
                    message.toString(), formatExceptionInfo(e));
            return false;
        }

        return true;
    }

    private RequestServiceInfo getRequestServiceInfo(String uri) throws Exception {
        Map<String, List<RequestServiceInfo>> requestServiceInfoMap = null;

        for(Map.Entry<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoEntry
                : ServiceData.getRequestServiceInfoMap().entrySet()) {
            String key = requestServiceInfoEntry.getKey();
            if(key.equals(uri) || key.contains("||" + uri + "||")) {
                requestServiceInfoMap = requestServiceInfoEntry.getValue();
                break;
            }
        }

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

}
