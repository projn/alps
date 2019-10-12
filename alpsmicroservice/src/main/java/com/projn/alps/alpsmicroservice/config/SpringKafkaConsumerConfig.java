package com.projn.alps.alpsmicroservice.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * spring kafka consumer config
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableKafka
@PropertySource(value = "file:${config.dir}/config/kafka-consumer.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.mq.consumer", havingValue = "true", matchIfMissing = true)
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
    private int concurrencyNum = 0;@Value("${kafka.consumer.sasl}")
    private boolean sasl = false;

    @Value("${kafka.ssl.key-store-location}")
    private String sslKeyStoreLocation = null;

    @Value("${kafka.ssl.key-store-password}")
    private String sslKeyStorePassword = null;

    @Value("${kafka.ssl.key-password}")
    private String sslKeyPassword = null;

    @Value("${kafka.ssl.trust-store-location}")
    private String sslTrustStoreLocation = null;

    @Value("${kafka.ssl.trust-store-password}")
    private String sslTrustStorePassword = null;

    @Value("${kafka.ssl.key-store-type}")
    private String sslKeyStoreType = null;

    @Value("${kafka.consumer.security.protocol}")
    private String securityProtocol = null;

    @Value("${kafka.consumer.sasl.mechanism}")
    private String saslMechanism = null;

    @Value("${kafka.consumer.sasl.jaas.config}")
    private String saslJaasConfig = null;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(Boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public Integer getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(Integer maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

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

        notNullAdd(properties, ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        notNullAdd(properties, ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        if (this.sasl) {
            notNullAdd(properties, SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, this.sslKeyStoreLocation);
            notNullAdd(properties, SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, this.sslKeyStorePassword);
            notNullAdd(properties, SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, this.sslTrustStoreLocation);
            notNullAdd(properties, SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, this.sslTrustStorePassword);
            notNullAdd(properties, SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, this.sslKeyStoreType);

            notNullAdd(properties, CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, this.securityProtocol);
            notNullAdd(properties, SaslConfigs.SASL_MECHANISM, this.saslMechanism);
            notNullAdd(properties, SaslConfigs.SASL_JAAS_CONFIG, this.saslJaasConfig);
        }
        return properties;
    }

    private void notNullAdd(Map<String, Object> properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value.toString());
        }
    }

    /**
     * kafka listener container factory
     *
     * @return ConcurrentKafkaListenerContainerFactory<String   ,       String> :
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrencyNum);
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(sessionTimeoutMs);
        return factory;
    }

    /**
     * consumer factory
     *
     * @return ConsumerFactory<String   ,       String> :
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(toProperties());
    }

}
