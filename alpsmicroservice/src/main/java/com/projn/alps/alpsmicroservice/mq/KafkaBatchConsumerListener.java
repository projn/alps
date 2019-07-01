package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

/**
 * kafka batch consumer listener
 *
 * @author : sunyuecheng
 */
public class KafkaBatchConsumerListener implements BatchMessageListener<String, String> {

    private MsgControllerTools msgControllerTools;

    /**
     * kafka batch consumer listener
     *
     * @param msgControllerTools :
     */
    public KafkaBatchConsumerListener(MsgControllerTools msgControllerTools) {
        this.msgControllerTools = msgControllerTools;
    }

    /**
     * on message
     *
     * @param data
     */
    @Override
    public void onMessage(List<ConsumerRecord<String, String>> data) {
        for (ConsumerRecord<String, String> msg : data) {
            msgControllerTools.deal(msg, null);
        }
    }
}
