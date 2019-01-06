package com.projn.alps.struct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

/**
 * mq consumer info
 *
 * @author : sunyuecheng
 */
public class MqConsumerInfo {

    private String topic;

    private String method;

    private String tags;

    private MessageListener messageListener;

    private DefaultMQPushConsumer mqPushConsumer;


    /**
     * mq consumer info
     */
    public MqConsumerInfo() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public DefaultMQPushConsumer getMqPushConsumer() {
        return mqPushConsumer;
    }

    public void setMqPushConsumer(DefaultMQPushConsumer mqPushConsumer) {
        this.mqPushConsumer = mqPushConsumer;
    }
}