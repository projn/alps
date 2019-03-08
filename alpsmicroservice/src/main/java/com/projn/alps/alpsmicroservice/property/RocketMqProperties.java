package com.projn.alps.alpsmicroservice.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.ROCKET_MQ_CONSUME_MAX_DIFF_SIZE;

/**
 * rocket mq properties
 *
 * @author : sunyuecheng
 */
@Component
@ConfigurationProperties
@PropertySource(value = "file:${config.dir}/config/rocketmq.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.rocketmq", havingValue = "true", matchIfMissing = true)
public class RocketMqProperties {

    @Value("${rocketmq.queueServerAddress}")
    private String queueServerAddress = null;

    @Value("${rocketmq.consumeMessageBatchMaxSize}")
    private Integer consumeMessageBatchMaxSize = null;

    @Value("${rocketmq.maxReconsumeTimes}")
    private Integer maxReconsumeTimes = 1;

    @Value("${rocketmq.consumeMessageMaxDiffSize}")
    private long consumeMessageMaxDiffSize = ROCKET_MQ_CONSUME_MAX_DIFF_SIZE;

    public String getQueueServerAddress() {
        return queueServerAddress;
    }

    public void setQueueServerAddress(String queueServerAddress) {
        this.queueServerAddress = queueServerAddress;
    }

    public Integer getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(Integer consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public Integer getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public void setMaxReconsumeTimes(Integer maxReconsumeTimes) {
        this.maxReconsumeTimes = maxReconsumeTimes;
    }

    public long getConsumeMessageMaxDiffSize() {
        return consumeMessageMaxDiffSize;
    }

    public void setConsumeMessageMaxDiffSize(long consumeMessageMaxDiffSize) {
        this.consumeMessageMaxDiffSize = consumeMessageMaxDiffSize;
    }
}