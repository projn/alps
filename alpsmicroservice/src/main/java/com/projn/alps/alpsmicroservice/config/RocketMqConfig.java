package com.projn.alps.alpsmicroservice.config;

import com.projn.alps.alpsmicroservice.property.RocketMqProperties;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rocket mq config
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableConfigurationProperties({RocketMqProperties.class, RunTimeProperties.class})
public class RocketMqConfig {

    @Autowired
    private RocketMqProperties rocketMqProperties;

    @Autowired
    private RunTimeProperties runTimeProperties;

    /**
     * default mq producer
     *
     * @return org.apache.rocketmq.client.producer.DefaultMQProducer :
     * @throws MQClientException :
     */
    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(runTimeProperties.getMasterRole());
        producer.setNamesrvAddr(rocketMqProperties.getQueueServerAddress());
        producer.setVipChannelEnabled(false);
        producer.start();

        return producer;
    }
}
