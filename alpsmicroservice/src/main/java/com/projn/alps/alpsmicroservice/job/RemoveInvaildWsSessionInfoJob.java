package com.projn.alps.alpsmicroservice.job;

import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.dao.IAgentMasterInfoDao;
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

import java.util.List;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * remove invaild web socket session info job
 *
 * @author : sunyuecheng
 */
@Component("RemoveInvaildWsSessionInfoJob")
@ConditionalOnProperty(name = "system.bean.switch.websocket", havingValue = "true", matchIfMissing = true)
public class RemoveInvaildWsSessionInfoJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveInvaildWsSessionInfoJob.class);

    @Autowired
    @Qualifier("AgentMasterInfoDao")
    private IAgentMasterInfoDao agentMasterInfoDao;

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
        try {
            List<String> agentIdList = WsSessionInfoMap.getInstance().
                    removeInvaildWebSocketSessionInfo(runTimeProperties.getWsSessionTimeOutMinutes());

            for (String agentId : agentIdList) {
                agentMasterInfoDao.deleteAgentMasterInfo(agentId);
            }

        } catch (Exception e) {
            LOGGER.error("Remove invaild web socket connection error, error info({}).", formatExceptionInfo(e));
        }
    }
}
