package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 * kafka consumer listener
 *
 * @author : sunyuecheng
 */
public class KafkaConsumerListener implements MessageListener<String, String> {

    private MsgControllerTools msgControllerTools;

    /**
     * kafka consumer listener
     *
     * @param msgControllerTools :
     */
    public KafkaConsumerListener(MsgControllerTools msgControllerTools) {
        this.msgControllerTools = msgControllerTools;
    }

    /**
     * on message
     *
     * @param msg                   :
     */
    @Override
    public void onMessage(ConsumerRecord<String, String> msg) {
        msgControllerTools.deal(msg, null);
    }
}
