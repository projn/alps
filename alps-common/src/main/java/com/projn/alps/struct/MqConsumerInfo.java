package com.projn.alps.struct;

import org.springframework.kafka.listener.KafkaMessageListenerContainer;

/**
 * mq consumer info
 *
 * @author : sunyuecheng
 */
public class MqConsumerInfo {

    private String topic;

    private String method;

    private KafkaMessageListenerContainer kafkaMessageListenerContainer;

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

    public KafkaMessageListenerContainer getKafkaMessageListenerContainer() {
        return kafkaMessageListenerContainer;
    }

    public void setKafkaMessageListenerContainer(KafkaMessageListenerContainer kafkaMessageListenerContainer) {
        this.kafkaMessageListenerContainer = kafkaMessageListenerContainer;
    }
}