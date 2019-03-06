package com.projn.alps.alpsmicroservice.work;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.initialize.InitializeBean;
import com.projn.alps.service.IComponentsMsgService;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.util.CounterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * worker process
 *
 * @author : sunyuecheng
 */
public class MsgProcessWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgProcessWorker.class);

    private String serviceName = null;
    private long bornTimestamp = 0;
    private MsgRequestInfo msgRequestInfo = null;

    /**
     * msg process worker
     *
     * @param serviceName    :
     * @param msgRequestInfo :
     * @param bornTimestamp  :
     */
    public MsgProcessWorker(String serviceName, MsgRequestInfo msgRequestInfo, long bornTimestamp) {
        this.bornTimestamp = bornTimestamp;
        this.msgRequestInfo = msgRequestInfo;
    }

    /**
     * run
     */
    @Override
    public void run() {
        if (StringUtils.isEmpty(serviceName) || msgRequestInfo == null) {
            LOGGER.error("Invaild request info error,,service name({}), request info({}).",
                    serviceName, JSON.toJSONString(msgRequestInfo));
            return;
        }

        try {
            IComponentsMsgService bean = InitializeBean.getBean(serviceName);
            if (bean == null) {
                LOGGER.error("Invaild service name error,service name({}).", serviceName);
                return;
            } else {
                long start = System.currentTimeMillis();

                bean.execute(bornTimestamp, msgRequestInfo);

                long end = System.currentTimeMillis();
                CounterUtils.increaseNum(String.format(MicroServiceDefine.MSG_CONSUME_COUNT_HEADER,
                        msgRequestInfo.getId()));
                CounterUtils.increaseNum(String.format(MicroServiceDefine.MSG_CONSUME_TOTAL_TIME_HEADER,
                        msgRequestInfo.getId()),
                        (double) (end - start));
                CounterUtils.recordMaxNum(String.format(MicroServiceDefine.MSG_CONSUME_MAX_TIME_HEADER,
                        msgRequestInfo.getId()),
                        (double) (end - start));
            }
        } catch (Exception e) {
            LOGGER.error("Deal request info error ,error info({}).", formatExceptionInfo(e));
            return;
        }
    }
}
