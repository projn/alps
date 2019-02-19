package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.projn.alps.struct.MsgRequestInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * order msg producer
 *
 * @author : sunyuecheng
 */
@Component
@ConditionalOnProperty(name = "bean.switch.rocketmq", havingValue = "true", matchIfMissing=true)
public class OrderMsgProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderMsgProducer.class);

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * add queue
     *
     * @param topic          :
     * @param tag            :
     * @param orderId        :
     * @param msgRequestInfo :
     * @return boolean :
     */
    synchronized public boolean addQueue(String topic, String tag, int orderId, MsgRequestInfo msgRequestInfo) {
        if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(tag) || msgRequestInfo == null) {
            LOGGER.error("Error param.");
            return false;
        }

        boolean ret = true;
        try {
            String body = JSON.toJSONString(msgRequestInfo);

            Message msg = new Message(topic, tag, body.getBytes(DEFAULT_ENCODING));
            SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object org) {
                    Integer orderId = (Integer) org;
                    int index = orderId % list.size();
                    return list.get(index);
                }
            }, Math.abs(orderId + 1));
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                LOGGER.error("Send message error,error info({}).", sendResult.getSendStatus());
                ret = false;
            }
        } catch (Exception e) {
            LOGGER.error("Send message error,error info({}),", formatExceptionInfo(e));
            ret = false;
        }
        return ret;
    }
}
