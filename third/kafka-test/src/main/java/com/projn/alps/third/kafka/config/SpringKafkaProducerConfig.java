package com.projn.alps.third.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@PropertySource("classpath:config/kafka-producer.properties")
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

    @Value("${kafka.producer.ssl.key.password}")
    private String sslKeyPassword = null;

    @Value("${kafka.producer.ssl.keystore.location}")
    private String sslKeystoreLocation = null;

    @Value("${kafka.producer.ssl.keystore.password}")
    private String sslKeystorePassword = null;

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
        return properties;
    }

    private void notNullAdd(Map<String, Object> properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value.toString());
        }
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<String, String>(toProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory(), autoFlush);
    }

}