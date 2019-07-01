package com.projn.alps.dao.impl;

import com.projn.alps.dao.IKafkaProducerInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * kafka producer info dao impl
 *
 * @author : sunyuecheng
 */
@Repository("KafkaProducerInfoDao")
public class KafkaProducerInfoDaoImpl implements IKafkaProducerInfoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerInfoDaoImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate = null;

    public KafkaTemplate getKafkaTemplate() {
        return kafkaTemplate;
    }

    public void setKafkaTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendAsyncMessageInfo(String topic, Object msg) {
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(topic, msg);

        SuccessCallback<SendResult<String, String>> successCallback
                = new SuccessCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("Send message to kafka successfully.");
            }
        };

        FailureCallback failureCallback = new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Send message to kafka error, error,info({}).", ex.getMessage());
            }
        };
        listenableFuture.addCallback(successCallback, failureCallback);
    }

    @Override
    public void sendAsyncMessageInfo(String topic, int partition, Object msg) {
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(topic, partition, msg);

        SuccessCallback<SendResult<String, String>> successCallback
                = new SuccessCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("Send message to kafka successfully.");
            }
        };

        FailureCallback failureCallback = new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Send message to kafka error, error,info({}).", ex.getMessage());
            }
        };
        listenableFuture.addCallback(successCallback, failureCallback);
    }

    @Override
    public void sendSyncMessageInfo(String topic, Object msg, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, msg).get(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendSyncMessageInfo(String topic, int partition, Object msg, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, partition, msg).get(timeout, TimeUnit.MILLISECONDS);
    }

}
