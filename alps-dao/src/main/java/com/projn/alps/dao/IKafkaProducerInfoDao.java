package com.projn.alps.dao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * kafka producer info dao
 *
 * @author : sunyuecheng
 */
public interface IKafkaProducerInfoDao {

    /**
     * send async message info
     *
     * @param topic :
     * @param msg   :
     */
    void sendAsyncMessageInfo(String topic, Object msg);

    /**
     * send async message info
     *
     * @param topic     :
     * @param partition :
     * @param msg       :
     */
    void sendAsyncMessageInfo(String topic, int partition, Object msg);

    /**
     * send sync message info
     *
     * @param topic   :
     * @param msg     :
     * @param timeout :
     * @throws InterruptedException :
     * @throws ExecutionException :
     * @throws TimeoutException :
     */
    void sendSyncMessageInfo(String topic, Object msg, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * send sync message info
     *
     * @param topic     :
     * @param partition :
     * @param msg       :
     * @param timeout   :
     * @throws InterruptedException :
     * @throws ExecutionException :
     * @throws TimeoutException :
     */
    void sendSyncMessageInfo(String topic, int partition, Object msg, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException;
}