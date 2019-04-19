package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.work.MsgProcessWorker;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * msg consumer listener
 *
 * @author : sunyuecheng
 */
public class MsgConsumerListener implements MessageListenerConcurrently {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgConsumerListener.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * msg consumer listener
     *
     * @param threadPoolTaskExecutor :
     */
    public MsgConsumerListener(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * consume message
     *
     * @param msgs    :
     * @param context :
     * @return ConsumeConcurrentlyStatus :
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

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

            String uri = msg.getTopic() + "/" + msg.getTags();
            Map<String, RequestServiceInfo> requestServiceInfoMap = ServiceData.getRequestServiceInfoMap().get(uri);
            if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()) {
                LOGGER.error("Invaild request service info, msg id (" + msg.getTags() + ").");
                continue;
            }

            for (Map.Entry<String, RequestServiceInfo> item : requestServiceInfoMap.entrySet()) {
                RequestServiceInfo requestServiceInfo = item.getValue();
                if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_MSG)) {
                    LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
                    continue;
                }

                MsgRequestInfo targetMsgRequestInfo = msgRequestInfo;
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
                        targetMsgRequestInfo.setId(msgRequestInfo.getId());
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

                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                } catch (Exception e) {
                    LOGGER.error("Analyse request info error,msg info({}),error info({}).",
                            JSON.toJSONString(msg), formatExceptionInfo(e));

                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }
}
