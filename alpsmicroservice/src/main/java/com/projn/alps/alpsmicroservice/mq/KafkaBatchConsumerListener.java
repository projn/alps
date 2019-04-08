package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

public class KafkaBatchConsumerListener implements BatchMessageListener<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBatchConsumerListener.class);

    private MsgControllerTools msgControllerTools;

    @Override
    public void onMessage(List<ConsumerRecord<String, String>> data) {
        for (ConsumerRecord<String, String> msg : data) {
            msgControllerTools.deal(msg, null);
        }
    }
}
