package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.work.MsgProcessWorker;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.tool.HttpControllerTools;
import com.projn.alps.tool.MsgControllerTools;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Future;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

public class KafkaAckConsumerListener implements AcknowledgingMessageListener<String,String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAckConsumerListener.class);

    private MsgControllerTools msgControllerTools;

    @Override
    public void onMessage(ConsumerRecord<String, String> msg, Acknowledgment acknowledgment) {
        msgControllerTools.deal(msg, acknowledgment);
    }
}
