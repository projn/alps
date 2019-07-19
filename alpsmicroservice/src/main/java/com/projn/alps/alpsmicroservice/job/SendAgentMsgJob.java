package com.projn.alps.alpsmicroservice.job;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.dao.IAgentMessageInfoDao;
import com.projn.alps.domain.AgentMessageInfo;
import com.projn.alps.msg.response.WsResponseMsgInfo;
import com.projn.alps.widget.WsSessionInfoMap;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * send agent msg job
 *
 * @author : sunyuecheng
 */
@Component("SendAgentMsgJob")
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing = true)
public class SendAgentMsgJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendAgentMsgJob.class);

    @Autowired
    @Qualifier("AgentMessageInfoDao")
    private IAgentMessageInfoDao agentMessageInfoDao;

    @Autowired
    private RunTimeProperties runTimeProperties;

    /**
     * execute
     *
     * @param context :
     * @throws JobExecutionException :
     */
    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        Set<String> agentIdList = WsSessionInfoMap.getInstance().getWebSocketSessionAgentIdList();
        if (agentIdList == null) {
            return;
        }
        for (String agentId : agentIdList) {
            AgentMessageInfo agentMessageInfo = agentMessageInfoDao.getAgentOrderMessageInfo(agentId);
            if (agentMessageInfo != null && (agentMessageInfo.getExpireTime() == null
                    || agentMessageInfo.getExpireTime() > System.currentTimeMillis())) {
                WsResponseMsgInfo wsResponseMsgInfo =
                        new WsResponseMsgInfo(agentMessageInfo.getMsgId(), agentMessageInfo.getMsg());
                try {
                    WsSessionInfoMap.getInstance().sendWebSocketMessageInfo(agentId,
                            JSON.toJSONString(wsResponseMsgInfo));
                    LOGGER.info("SEND MSG TO AGENT:" + agentId + "---" + JSON.toJSONString(wsResponseMsgInfo));
                } catch (Exception e) {
                    LOGGER.error("Send agent msg info error,error info({}).", formatExceptionInfo(e));
                }
            }

            if (runTimeProperties.getWsMsgIdList() != null) {
                for (String msgId : runTimeProperties.getWsMsgIdList()) {
                    agentMessageInfo = agentMessageInfoDao.getAgentCoverMessageInfo(agentId, msgId);
                    if (agentMessageInfo == null) {
                        continue;
                    }
                    WsResponseMsgInfo wsResponseMsgInfo =
                            new WsResponseMsgInfo(agentMessageInfo.getMsgId(), agentMessageInfo.getMsg());
                    try {
                        WsSessionInfoMap.getInstance().sendWebSocketMessageInfo(agentId,
                                JSON.toJSONString(wsResponseMsgInfo));
                        agentMessageInfoDao.deleteAgentCoverMessageInfo(agentId, msgId);
                        LOGGER.info("SEND MSG TO AGENT:" + agentId + "---" + JSON.toJSONString(wsResponseMsgInfo));
                        break;
                    } catch (Exception e) {
                        LOGGER.error("Send agent msg info error,error info({}).", formatExceptionInfo(e));
                    }
                }
            }
        }
    }
}
