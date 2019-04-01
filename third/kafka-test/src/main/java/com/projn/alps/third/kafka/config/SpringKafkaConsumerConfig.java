package com.projn.alps.third.kafka.config;

import com.projn.alps.third.kafka.listener.KafkaConsumerListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@PropertySource("classpath:config/kafka-consumer.properties")
public class SpringKafkaConsumerConfig {

    @Value("${kafka.consumer.bootstrap.servers}")
    private String bootstrapServers = null;

    @Value("${kafka.consumer.group.id}")
    private String groupId = null;

    @Value("${kafka.consumer.session.timeout.ms}")
    private Long sessionTimeoutMs = 10000L;

    @Value("${kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset = "latest";

    @Value("${kafka.consumer.connections.max.idle.ms}")
    private Long connectionsMaxIdleMs = 540000L;

    @Value("${kafka.consumer.enable.auto.commit}")
    private Boolean enableAutoCommit = true;

    @Value("${kafka.consumer.fetch.max.bytes}")
    private Long fetchMaxBytes = 52428800L;

    @Value("${kafka.consumer.max.poll.interval.ms}")
    private Long maxPollIntervalMs = 300000L;

    @Value("${kafka.consumer.max.poll.records}")
    private Integer maxPollRecords = 500;

    @Value("${kafka.consumer.request.timeout.ms}")
    private Long requestTimeoutMs = 30000L;

    @Value("${kafka.consumer.concurrency.num}")
    private int concurrencyNum = 0;

    private Map<String, Object> toProperties() {
        Map<String, Object> properties = new HashMap<>();
        notNullAdd(properties, ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        notNullAdd(properties, ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        notNullAdd(properties, ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, this.sessionTimeoutMs);
        notNullAdd(properties, ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetReset);
        notNullAdd(properties, ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, this.connectionsMaxIdleMs);
        notNullAdd(properties, ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, this.enableAutoCommit);
        notNullAdd(properties, ConsumerConfig.FETCH_MAX_BYTES_CONFIG, this.fetchMaxBytes);
        notNullAdd(properties, ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, this.maxPollIntervalMs);
        notNullAdd(properties, ConsumerConfig.MAX_POLL_RECORDS_CONFIG, this.maxPollRecords);
        notNullAdd(properties, ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.requestTimeoutMs);
        return properties;
    }

    private void notNullAdd(Map<String, Object> properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value.toString());
        }
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrencyNum);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(sessionTimeoutMs);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(toProperties());
    }

    @Bean
    public KafkaMessageListenerContainer addListenerContainer() {
        ContainerProperties properties = new ContainerProperties("topic");
        properties.setGroupId("group.id");
        properties.setMessageListener(new KafkaConsumerListener());

        return new KafkaMessageListenerContainer(consumerFactory(), properties);
    }

}