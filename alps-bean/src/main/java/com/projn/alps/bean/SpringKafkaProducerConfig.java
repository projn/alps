package com.projn.alps.bean;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * spring kafka producer config
 *
 * @author : sunyuecheng
 */
@Configuration
@EnableKafka
@PropertySource(value = "file:${config.dir}/config/kafka-producer.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.mq.producer", havingValue = "true", matchIfMissing = true)
public class SpringKafkaProducerConfig {

    @Value("${kafka.producer.acks}")
    private Integer acks = 1;

    @Value("${kafka.producer.bootstrap.servers}")
    private String bootstrapServers = null;

    @Value("${kafka.producer.buffer.memory}")
    private Long bufferMemory = 33554432L;

    @Value("${kafka.producer.compression.type}")
    private String compressionType = "none";

    @Value("${kafka.producer.retries}")
    private Integer retries = 0;

    @Value("${kafka.producer.batch.size}")
    private Long batchSize = 16384L;

    @Value("${kafka.producer.client.id}")
    private String clientId = null;

    @Value("${kafka.producer.connections.max.idle.ms}")
    private Long connectionsMaxIdleMs = 540000L;

    @Value("${kafka.producer.delivery.timeout.ms}")
    private Long deliveryTimeoutMs = 120000L;

    @Value("${kafka.producer.linger.ms}")
    private Long lingerMs = 0L;

    @Value("${kafka.producer.max.block.ms}")
    private Long maxBlockMs = 60000L;

    @Value("${kafka.producer.max.request.size}")
    private Integer maxRequestSize = 1048576;

    @Value("${kafka.producer.request.timeout.ms}")
    private Long requestTimeoutMs = 30000L;

    @Value("${kafka.producer.autoflush}")
    private boolean autoFlush = true;

    @Value("${kafka.producer.topic.partition.size}")
    private int topicPartitionSize = 4;

    @Value("${kafka.producer.sasl}")
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

    @Value("${kafka.producer.security.protocol}")
    private String securityProtocol = null;

    @Value("${kafka.producer.sasl.mechanism}")
    private String saslMechanism = null;

    @Value("${kafka.producer.sasl.jaas.config}")
    private String saslJaasConfig = null;

    public int getTopicPartitionSize() {
        return topicPartitionSize;
    }

    public void setTopicPartitionSize(int topicPartitionSize) {
        this.topicPartitionSize = topicPartitionSize;
    }

    private Map<String, Object> toProperties() {
        Map<String, Object> properties = new HashMap<>();
        notNullAdd(properties, ProducerConfig.ACKS_CONFIG, this.acks);
        notNullAdd(properties, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        notNullAdd(properties, ProducerConfig.BUFFER_MEMORY_CONFIG, this.bufferMemory);
        notNullAdd(properties, ProducerConfig.COMPRESSION_TYPE_CONFIG, this.compressionType);
        notNullAdd(properties, ProducerConfig.RETRIES_CONFIG, this.retries);
        notNullAdd(properties, ProducerConfig.BATCH_SIZE_CONFIG, this.batchSize);
        notNullAdd(properties, ProducerConfig.CLIENT_ID_CONFIG, this.clientId);
        notNullAdd(properties, ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, this.connectionsMaxIdleMs);
        notNullAdd(properties, ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, this.deliveryTimeoutMs);
        notNullAdd(properties, ProducerConfig.LINGER_MS_CONFIG, this.lingerMs);
        notNullAdd(properties, ProducerConfig.MAX_BLOCK_MS_CONFIG, this.maxBlockMs);
        notNullAdd(properties, ProducerConfig.MAX_REQUEST_SIZE_CONFIG, this.maxRequestSize);
        notNullAdd(properties, ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.requestTimeoutMs);

        notNullAdd(properties, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        notNullAdd(properties, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

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
     * producer factory
     *
     * @return PProducerFactory<String       ,               String> :
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<String, String>(toProperties());
    }

    /**
     * kafka template
     *
     * @return KafkaTemplate<String       ,               String> :
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory(), autoFlush);
    }

}
