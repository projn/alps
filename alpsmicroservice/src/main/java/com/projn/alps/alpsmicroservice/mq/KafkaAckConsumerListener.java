package com.projn.alps.alpsmicroservice.mq;

import com.projn.alps.tool.MsgControllerTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * kafka ack consumer listener
 *
 * @author : sunyuecheng
 */
public class KafkaAckConsumerListener implements AcknowledgingMessageListener<String, String> {

    private MsgControllerTools msgControllerTools;

    /**
     * kafka ack consumer listener
     *
     * @param msgControllerTools :
     */
    public KafkaAckConsumerListener(MsgControllerTools msgControllerTools) {
        this.msgControllerTools = msgControllerTools;
    }

    /**
     * on message
     *
     * @param msg            :
     * @param acknowledgment :
     */
    @Override
    public void onMessage(ConsumerRecord<String, String> msg, Acknowledgment acknowledgment) {
        msgControllerTools.deal(msg, acknowledgment);
    }
}
