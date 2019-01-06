package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.alpsmicroservice.work.MsgProcessWorker;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.ParamCheckUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

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
@Component
public class OrderMsgConsumerListener implements MessageListenerOrderly {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMsgConsumerListener.class);

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void setDefaultMQPushConsumer(DefaultMQPushConsumer defaultMQPushConsumer) {
        this.defaultMQPushConsumer = defaultMQPushConsumer;
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
            if (msgRequestInfo == null) {
                continue;
            }

            Map<String, RequestServiceInfo> requestServiceInfoMap
                    = ServiceData.getRequestServiceInfoMap().get(String.valueOf(msgRequestInfo.getId()));
            if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()) {
                LOGGER.error("Invaild request service info, msg id (" + msgRequestInfo.getId() + ").");
                continue;
            }

            for (Map.Entry<String, RequestServiceInfo> item : requestServiceInfoMap.entrySet()) {
                RequestServiceInfo requestServiceInfo = item.getValue();
                if(!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_WS)) {
                    LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType()+").");
                    continue;
                }

                Object paramObj = null;
                if (requestServiceInfo.getParamClass() != null) {

                    try {
                        paramObj = JSONObject.parseObject(JSON.toJSONString(msgRequestInfo.getMsg()),
                                requestServiceInfo.getParamClass());
                    } catch (Exception e) {
                        LOGGER.error("Convert request info error,error info(" + e.getMessage() + ").");
                        continue;
                    }

                    if (paramObj != null) {
                        try {
                            ParamCheckUtils.checkParam(paramObj);
                        } catch (Exception e) {
                            LOGGER.error("Check param error,error info(" + e.getMessage() + ").");
                            continue;
                        }
                    }
                }

                MsgRequestInfo targetMsgRequestInfo
                        = new MsgRequestInfo(msgRequestInfo.getId(), paramObj, msgRequestInfo.getExtendInfoMap());
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
                            JSON.toJSONString(msgRequestInfo.getMsg()), formatExceptionInfo(e));
                    try {
                        defaultMQPushConsumer.sendMessageBack(msg, MicroServiceDefine.MQ_DELAY_LEVEL);
                    } catch (Exception ex) {
                        LOGGER.error("Send msg error,error info({}).", formatExceptionInfo(ex));
                    }
                }
            }
        }

        return ConsumeOrderlyStatus.SUCCESS;
    }
}
