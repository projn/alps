package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.alpsmicroservice.work.MsgProcessWorker;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;
import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * order msg consumer listener
 *
 * @author : sunyuecheng
 */
public class OrderMsgConsumerListener implements MessageListenerOrderly {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMsgConsumerListener.class);

    private DefaultMQPushConsumer defaultMQPushConsumer;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * order msg consumer listener
     *
     * @param defaultMQPushConsumer  :
     * @param threadPoolTaskExecutor :
     */
    public OrderMsgConsumerListener(DefaultMQPushConsumer defaultMQPushConsumer,
                                    ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.defaultMQPushConsumer = defaultMQPushConsumer;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * consume message
     *
     * @param msgs    :
     * @param context :
     * @return org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus :
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (MessageExt msg : msgs) {
            MsgRequestInfo msgRequestInfo = null;
            try {
                String body = new String(msg.getBody(), DEFAULT_ENCODING);
                msgRequestInfo = (MsgRequestInfo) JSONObject.parseObject(body, MsgRequestInfo.class);
            } catch (Exception e) {
                LOGGER.error("Convert object error,error info({}).", formatExceptionInfo(e));
                continue;
            }

            String uri = msg.getTopic() + "/" + msg.getTags();
            Map<String, List<RequestServiceInfo>> requestServiceInfoMap
                    = ServiceData.getRequestServiceInfoMap().get(uri);
            if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()) {
                LOGGER.error("Invaild request service info, msg id (" + msg.getTags() + ").");
                continue;
            }

            for (Map.Entry<String, List<RequestServiceInfo>> item : requestServiceInfoMap.entrySet()) {
                List<RequestServiceInfo> requestServiceInfoList = item.getValue();
                for (RequestServiceInfo requestServiceInfo : requestServiceInfoList) {
                    if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_MSG)) {
                        LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
                        continue;
                    }

                    MsgRequestInfo targetMsgRequestInfo = null;
                    if (requestServiceInfo.getParamClass() != null) {
                        try {
                            String msgText = JSON.toJSONString(msgRequestInfo.getMsg());
                            targetMsgRequestInfo = RequestInfoUtils.convertMsgRequestInfo(
                                    msgText, requestServiceInfo.getParamClass());
                        } catch (Exception e) {
                            LOGGER.error("Convert request info error,error info(" + e.getMessage() + ").");
                            continue;
                        }
                        if (targetMsgRequestInfo != null && targetMsgRequestInfo.getMsg() != null) {
                            try {
                                ParamCheckUtils.checkParam(targetMsgRequestInfo.getMsg());
                            } catch (Exception e) {
                                LOGGER.error("Check param error,error info(" + e.getMessage() + ").");
                                continue;
                            }
                        }
                        if (targetMsgRequestInfo != null) {
                            targetMsgRequestInfo.setId(Integer.parseInt(msg.getTags()));
                            targetMsgRequestInfo.setExtendInfoMap(msgRequestInfo.getExtendInfoMap());
                        }
                    }

                    try {
                        if (threadPoolTaskExecutor.getActiveCount() < threadPoolTaskExecutor.getMaxPoolSize()) {
                            Future<?> future = threadPoolTaskExecutor.submit(
                                    new MsgProcessWorker(requestServiceInfo.getServiceName(),
                                            targetMsgRequestInfo, msg.getBornTimestamp()));
                            future.isDone();
                        } else {
                            LOGGER.debug("System is busy.");
                            context.setSuspendCurrentQueueTimeMillis(
                                    MicroServiceDefine.MQ_SUSPEND_SECOUND * MILLI_SECOND_1000);
                            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Analyse request info error,msg info({}),error info({}).",
                                JSON.toJSONString(msg), formatExceptionInfo(e));
                        try {
                            defaultMQPushConsumer.sendMessageBack(msg, MicroServiceDefine.MQ_DELAY_LEVEL);
                        } catch (Exception ex) {
                            LOGGER.error("Send msg error,error info({}).", formatExceptionInfo(ex));
                        }
                    }
                }
            }
        }

        return ConsumeOrderlyStatus.SUCCESS;
    }
}
