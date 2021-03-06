package com.projn.alps.sender;

import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.dao.IAgentMessageInfoDao;
import com.projn.alps.domain.AgentMasterInfo;
import com.projn.alps.domain.AgentMessageInfo;
import com.projn.alps.msg.request.HttpSendMsgRequestMsgInfo;
import com.projn.alps.msg.response.HttpSendMsgResponseMsgInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.projn.alps.define.HttpDefine.HTTP_URL_HEADER;

/**
 * agent msg sender
 *
 * @author : sunyuecheng
 */
@Component("AgentMsgSender")
public class AgentMsgSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMsgSender.class);

    @Autowired
    @Qualifier("AgentMessageInfoDao")
    private IAgentMessageInfoDao agentMessageInfoDao;

    @Autowired
    @Qualifier("AgentMasterInfoDao")
    private IAgentMasterInfoDao agentMasterInfoDao;

    @Autowired
    @Qualifier("sslRestTemplate")
    private RestTemplate sslRestTemplate;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    /**
     * boolean send order msg without response
     *
     * @param agentId :
     * @param msgId   :
     * @param msg     :
     * @return boolean :
     */
    public boolean sendOrderMsgWithoutResponse(String agentId, String msgId, Object msg) {
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(msgId) || msg == null) {
            LOGGER.error("Error param.");
            return false;
        }
        return agentMessageInfoDao.setAgentOrderMessageInfo(new AgentMessageInfo(agentId, msgId, msg));
    }

    /**
     * send order msg without response
     *
     * @param agentId        :
     * @param msgId          :
     * @param msg            :
     * @param timeoutSeconds :
     * @return boolean :
     */
    public boolean sendOrderMsgWithoutResponse(String agentId, String msgId, Object msg, long timeoutSeconds) {
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(msgId) || msg == null) {
            LOGGER.error("Error param.");
            return false;
        }
        return agentMessageInfoDao.setAgentOrderMessageInfo(
                new AgentMessageInfo(agentId, msgId, msg, timeoutSeconds));
    }

    /**
     * send cover msg without response
     *
     * @param agentId :
     * @param msgId   :
     * @param msg     :
     * @return boolean :
     */
    public boolean sendCoverMsgWithoutResponse(String agentId, String msgId, Object msg) {
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(msgId) || msg == null) {
            LOGGER.error("Error param.");
            return false;
        }
        return agentMessageInfoDao.setAgentCoverMessageInfo(new AgentMessageInfo(agentId, msgId, msg));
    }

    /**
     * send cover msg without response
     *
     * @param agentId        :
     * @param msgId          :
     * @param msg            :
     * @param timeoutSeconds :
     * @return boolean :
     */
    public boolean sendCoverMsgWithoutResponse(String agentId, String msgId, Object msg, long timeoutSeconds) {
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(msgId) || msg == null) {
            LOGGER.error("Error param.");
            return false;
        }
        return agentMessageInfoDao.setAgentCoverMessageInfo(
                new AgentMessageInfo(agentId, msgId, msg, timeoutSeconds));
    }

    /**
     * send msg with response
     *
     * @param agentId :
     * @param msgId   :
     * @param msg     :
     * @return HttpSendMsgResponseMsgInfo :
     */
    public HttpSendMsgResponseMsgInfo sendMsgWithResponse(String agentId, String msgId, Object msg) {
        if (StringUtils.isEmpty(agentId) || StringUtils.isEmpty(msgId) || msg == null) {
            LOGGER.error("Error param.");
            return null;
        }

        AgentMasterInfo agentMasterInfo = agentMasterInfoDao.getAgentMasterInfo(agentId);
        if (agentMasterInfo == null || StringUtils.isEmpty(agentMasterInfo.getApiUrl())) {
            LOGGER.error("Invaild agent connect info.");
            return new HttpSendMsgResponseMsgInfo(agentId, HttpSendMsgResponseMsgInfo.SEND_STATUS_AGENT_OFF_LINE);
        }

        ResponseEntity<HttpSendMsgResponseMsgInfo> responseEntity = null;
        if (agentMasterInfo.getApiUrl().startsWith(HTTP_URL_HEADER)) {
            responseEntity = restTemplate.postForEntity(agentMasterInfo.getApiUrl(),
                    new HttpSendMsgRequestMsgInfo(agentId, msgId, msg), HttpSendMsgResponseMsgInfo.class);
        } else {
            responseEntity = sslRestTemplate.postForEntity(agentMasterInfo.getApiUrl(),
                    new HttpSendMsgRequestMsgInfo(agentId, msgId, msg), HttpSendMsgResponseMsgInfo.class);
        }

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            LOGGER.error("Send agent msg info error, http status({}).", responseEntity.getStatusCode());
        }

        return null;
    }
}
