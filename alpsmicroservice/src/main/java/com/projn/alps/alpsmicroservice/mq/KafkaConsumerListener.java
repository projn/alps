package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

public class KafkaConsumerListener implements MessageListener<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerListener.class);

    private MsgControllerTools msgControllerTools;

    @Override
    public void onMessage(ConsumerRecord<String, String> msg) {
        msgControllerTools.deal(msg, null);
    }
}
