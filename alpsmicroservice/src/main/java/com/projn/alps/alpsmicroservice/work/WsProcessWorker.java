package com.projn.alps.alpsmicroservice.work;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.alpsmicroservice.widget.WsSessionInfoMap;
import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.dao.impl.AgentMasterInfoDaoImpl;
import com.projn.alps.domain.AgentMasterInfo;
import com.projn.alps.initialize.InitializeBean;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.service.IComponentsWsService;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;
import com.projn.alps.util.CounterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.*;
import static com.projn.alps.define.CommonDefine.MSG_RESPONSE_MAX_TIME_HEADER;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * ws process worker
 *
 * @author : sunyuecheng
 */
public class WsProcessWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsProcessWorker.class);

    private String serviceName = null;
    private WsRequestInfo wsRequestInfo = null;
    private WebSocketSession session = null;

    /**
     * ws process worker
     *
     * @param serviceName   :
     * @param wsRequestInfo :
     * @param session       :
     */
    public WsProcessWorker(String serviceName, WsRequestInfo wsRequestInfo, WebSocketSession session) {
        this.serviceName = serviceName;
        this.wsRequestInfo = wsRequestInfo;
        this.session = session;
    }

    /**
     * run
     */
    @Override
    public void run() {
        if (StringUtils.isEmpty(serviceName) || wsRequestInfo == null || session == null) {
            LOGGER.error("Invaild request info error,service name({}), request info({}).",
                    serviceName, JSON.toJSONString(wsRequestInfo));
            return;
        }
        WsResponseInfo wsResponseInfo = null;
        try {
            IComponentsWsService bean = InitializeBean.getBean(serviceName);
            if (bean == null) {
                LOGGER.error("Invaild service name error,service name({}).", serviceName);
                return;
            } else {
                long start = System.currentTimeMillis();

                wsResponseInfo = bean.execute(wsRequestInfo);

                long end = System.currentTimeMillis();
                CounterUtils.recordMaxNum(String.format(MSG_RESPONSE_MAX_TIME_HEADER,
                        serviceName), (double) (end - start));
            }
        } catch (Exception e) {
            LOGGER.error("Deal request info error ,error info({}).", formatExceptionInfo(e));
            return;
        }

        if (wsResponseInfo != null) {
            try {
                if (wsResponseInfo.getMsg() != null) {
                    TextMessage message = new TextMessage(JSON.toJSONString(wsResponseInfo.getMsg()));
                    session.sendMessage(message);
                }

                if (wsResponseInfo.getExtendInfoMap() != null) {
                    for (Map.Entry<String, Object> item : wsResponseInfo.getExtendInfoMap().entrySet()) {
                        session.getAttributes().put(item.getKey(), item.getValue());
                    }

                    String agentId = (String) wsResponseInfo.getExtendInfoMap().get(MicroServiceDefine.AGENT_ID_KEY);
                    if (!StringUtils.isEmpty(agentId)) {
                        if (!WsSessionInfoMap.getInstance().addWebSocketSessionInfo(agentId, session)) {
                            LOGGER.error("Add web socket session to pool error, pool size({}).",
                                    WsSessionInfoMap.getInstance().getPoolSize());
                            return;
                        }

                        IAgentMasterInfoDao agentMasterInfoDao = InitializeBean.getBean(AgentMasterInfoDaoImpl.class);
                        if(agentMasterInfoDao!=null) {
                            String url = ServiceData.getMasterInfo().isServerSsl()? HTTP_URL_HEADER: HTTPS_URL_HEADER
                                    + ServiceData.getMasterInfo().getServerIp() + ":"
                                    + ServiceData.getMasterInfo().getServerPort() + "/" + HTTP_API_SERVICE_SEND_MSG_URI;
                            agentMasterInfoDao.setAgentMasterInfo(new AgentMasterInfo(agentId,
                                    ServiceData.getMasterInfo().getServerIp(),
                                    ServiceData.getMasterInfo().getServerPort(),url));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Analyse response info error,service name({}), response info({}), error info({}).",
                        serviceName, JSON.toJSONString(wsResponseInfo), formatExceptionInfo(e));
                return;
            }
        }
    }
}
