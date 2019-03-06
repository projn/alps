package com.projn.alps.dao;

/**
 * rocket mq dao
 *
 * @author : sunyuecheng
 */
public interface IRocketMqDao {

    /**
     * send msg
     *
     * @param topic          :
     * @param tag            :
     * @param msgRequestInfo :
     * @return boolean :
     */
    boolean sendMsg(String topic, String tag, Object msgRequestInfo);

    /**
     * send order msg
     *
     * @param topic          :
     * @param tag            :
     * @param orderId        :
     * @param msgRequestInfo :
     * @return boolean :
     */
    boolean sendOrderMsg(String topic, String tag, int orderId, Object msgRequestInfo);
}
