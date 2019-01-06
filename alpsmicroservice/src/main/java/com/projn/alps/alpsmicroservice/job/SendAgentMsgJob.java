package com.projn.alps.alpsmicroservice.job;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.widget.WsSessionInfoMap;
import com.projn.alps.dao.IAgentMessageInfoDao;
import com.projn.alps.domain.AgentMessageInfo;
import com.projn.alps.msg.response.WsResponseMsgInfo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.projn.alps.define.CommonDefine.MAX_AGENT_MSG_ID;
import static com.projn.alps.define.CommonDefine.MIN_AGENT_MSG_ID;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * send agent msg job
 * @author : sunyuecheng
 */
@Component("SendAgentMsgJob")
public class SendAgentMsgJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendAgentMsgJob.class);

    @Autowired
    @Qualifier("AgentMessageInfoDao")
    private IAgentMessageInfoDao agentMessageInfoDao;

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

        if(agentIdList!=null) {
            for (String agentId : agentIdList) {
                AgentMessageInfo agentMessageInfo = agentMessageInfoDao.getAgentOrderMessageInfo(agentId);
                if(agentMessageInfo!=null) {
                    if(agentMessageInfo.getExpireTime() == null
                            || agentMessageInfo.getExpireTime()> System.currentTimeMillis()) {

                        WsResponseMsgInfo wsResponseMsgInfo =
                                new WsResponseMsgInfo(agentMessageInfo.getMsgId(), agentMessageInfo.getMsg());
                        try {
                            WsSessionInfoMap.getInstance().sendWebSocketMessageInfo(agentId,
                                    JSON.toJSONString(wsResponseMsgInfo));
                        } catch (Exception e) {
                            LOGGER.error("Send agent msg info error,error info({}).", formatExceptionInfo(e));
                        }
                    }
                }

                for(int i= MIN_AGENT_MSG_ID; i <=MAX_AGENT_MSG_ID; i++) {
                    agentMessageInfo = agentMessageInfoDao.getAgentCoverMessageInfo(agentId, i);
                    if(agentMessageInfo!=null) {
                        WsResponseMsgInfo webSocketResponseMessage =
                                new WsResponseMsgInfo(agentMessageInfo.getMsgId(), agentMessageInfo.getMsg());
                        try {
                            WsSessionInfoMap.getInstance().sendWebSocketMessageInfo(agentId,
                                    JSON.toJSONString(webSocketResponseMessage));
                            agentMessageInfoDao.deleteAgentCoverMessageInfo(agentId, i);
                        } catch (Exception e) {
                            LOGGER.error("Send agent msg info error,error info({}).", formatExceptionInfo(e));
                            continue;
                        }
                        break;
                    }
                }
            }
        }
    }
}
