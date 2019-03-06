package com.projn.alps.bean;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * rocket mq config
 *
 * @author : sunyuecheng
 */
@Configuration
@PropertySource(value = {"file:${config.dir}/config/rocketmq.properties",
        "file:${config.dir}/application.properties"}, ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.rocketmq", havingValue = "true", matchIfMissing=true)
public class RocketMqConfig {

    @Value("${spring.application.name}")
    private String appName = null;

    @Value("${rocketmq.queueServerAddress}")
    private String queueServerAddress = null;

    /**
     * default mq producer
     *
     * @return org.apache.rocketmq.client.producer.DefaultMQProducer :
     * @throws MQClientException :
     */
    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(appName);
        producer.setNamesrvAddr(queueServerAddress);
        producer.setVipChannelEnabled(false);
        producer.start();

        return producer;
    }
}
