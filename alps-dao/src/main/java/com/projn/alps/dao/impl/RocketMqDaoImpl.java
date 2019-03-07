package com.projn.alps.dao.impl;

import com.alibaba.fastjson.JSON;
import com.projn.alps.dao.IRocketMqDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * msg producer
 *
 * @author : sunyuecheng
 */
@Repository("RocketMqDao")
public class RocketMqDaoImpl implements IRocketMqDao{
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqDaoImpl.class);

    private static final String DEFAULT_ENCODING = "UTF-8";

    @Autowired(required = false)
    private DefaultMQProducer defaultMQProducer;

    /**
     * send msg
     *
     * @param topic          :
     * @param tag            :
     * @param msgRequestInfo :
     * @return boolean :
     */
    @Override
    synchronized public boolean sendMsg(String topic, String tag, Object msgRequestInfo) {
        if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(tag) || msgRequestInfo == null) {
            LOGGER.error("Error param.");
            return false;
        }

        boolean ret = true;
        try {
            String body = JSON.toJSONString(msgRequestInfo);

            Message msg = new Message(topic, tag, body.getBytes(DEFAULT_ENCODING));
            SendResult sendResult = defaultMQProducer.send(msg);
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                LOGGER.error("Send message error,error info({}).", sendResult.getSendStatus());
                ret = false;
            }
        } catch (Exception e) {
            LOGGER.error("Send message error,error info({}),", e.getMessage());
            ret = false;
        }
        return ret;
    }

    /**
     * send order msg
     *
     * @param topic          :
     * @param tag            :
     * @param orderId        :
     * @param msgRequestInfo :
     * @return boolean :
     */
    @Override
    synchronized public boolean sendOrderMsg(String topic, String tag, int orderId, Object msgRequestInfo) {
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
            LOGGER.error("Send message error,error info({}),", e.getMessage());
            ret = false;
        }
        return ret;
    }
}
