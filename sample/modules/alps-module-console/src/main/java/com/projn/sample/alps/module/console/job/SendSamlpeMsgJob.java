package com.projn.sample.alps.module.console.job;

import com.projn.alps.dao.IRocketMqDao;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.tool.QuartzJobTools;
import com.projn.sample.alps.module.console.struct.UserMsgInfoStructInfo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * send msg job
 *
 * @author :
 */
@Component("SendSamlpeMsgJob")
public class SendSamlpeMsgJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendSamlpeMsgJob.class);

    @Autowired
    @Qualifier("RocketMqDao")
    private IRocketMqDao rocketMqDao;

    private Long scanTimeIntervalSeconds = 0L;

    private boolean firstRun = true;

    public Long getScanTimeIntervalSeconds() {
        return scanTimeIntervalSeconds;
    }

    public void setScanTimeIntervalSeconds(Long scanTimeIntervalSeconds) {
        this.scanTimeIntervalSeconds = scanTimeIntervalSeconds;
    }

    /**
     * execute
     *
     * @param context :
     * @throws JobExecutionException :
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (firstRun) {
            try {
                QuartzJobTools.fillJobParams(this, context.getMergedJobDataMap());
                firstRun = false;
            } catch (Exception e) {
                LOGGER.error("Fill job params error, error info(" + formatExceptionInfo(e) + ").");
            }
        }

        String topic = "normal_msg";
        MsgRequestInfo msgRequestInfo = new MsgRequestInfo();
        UserMsgInfoStructInfo userMsgInfoStructInfo = new UserMsgInfoStructInfo();
        userMsgInfoStructInfo.setUserId(System.currentTimeMillis() + "");
        msgRequestInfo.setMsg(userMsgInfoStructInfo);

        String tag = "200";
        msgRequestInfo.setId(200);
        rocketMqDao.sendMsg(topic, tag, msgRequestInfo);
        topic = "order_msg";
        tag = "201";
        msgRequestInfo.setId(201);
        rocketMqDao.sendOrderMsg(topic, tag, 100, msgRequestInfo);
        topic = "broadcast_msg";
        tag = "202";
        msgRequestInfo.setId(202);
        rocketMqDao.sendMsg(topic, tag, msgRequestInfo);


        LOGGER.info("Job run.");

    }
}
