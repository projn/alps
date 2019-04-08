package com.projn.alps.third.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaAnnounceConsumerListener {

    @KafkaListener(id = "group.id", topics = "test")
    public void listener(String msg) {
    }

    @KafkaListener(id = "group.id", clientIdPrefix = "prefix", containerFactory = "kafkaListenerContainerFactory",
            topicPartitions = {
                    @TopicPartition(topic = "topic1", partitions = {"1", "3"}),
                    @TopicPartition(topic = "topic2", partitions = {"0", "4"},
                            partitionOffsets = @PartitionOffset(partition = "2", initialOffset = "100"))
            })
    public void batchListenerByPartition(List<String> data) {
    }

    @KafkaListener(id = "group.id", clientIdPrefix = "prefix", topics = {"topic"}, containerFactory = "kafkaListenerContainerFactory")
    public void batchListener(List<String> data) {

    }


    @KafkaListener(id = "group.id", topics = "topic")
    public void announceListener(@Payload String data,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {


    }

    @KafkaListener(id = "group.id", topics = "topic")
    public void consumerListener(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {


    }
}
