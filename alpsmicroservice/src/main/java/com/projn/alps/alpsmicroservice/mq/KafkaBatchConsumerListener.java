package com.projn.alps.alpsmicroservice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.work.MsgProcessWorker;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.tool.MsgControllerTools;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

public class KafkaBatchConsumerListener implements BatchMessageListener<String,String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBatchConsumerListener.class);

    private MsgControllerTools msgControllerTools;

    @Override
    public void onMessage(List<ConsumerRecord<String, String>> data) {
        for (ConsumerRecord<String, String> msg : data) {
            msgControllerTools.deal(msg, null);
        }
    }
}
