package com.projn.alps.third.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener implements MessageListener<String,String>{

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {

    }
}
