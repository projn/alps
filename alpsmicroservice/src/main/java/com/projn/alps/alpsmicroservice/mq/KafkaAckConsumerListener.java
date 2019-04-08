package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

public class KafkaAckConsumerListener implements AcknowledgingMessageListener<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAckConsumerListener.class);

    private MsgControllerTools msgControllerTools;

    @Override
    public void onMessage(ConsumerRecord<String, String> msg, Acknowledgment acknowledgment) {
        msgControllerTools.deal(msg, acknowledgment);
    }
}
