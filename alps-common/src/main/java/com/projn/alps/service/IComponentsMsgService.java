package com.projn.alps.service;

import com.projn.alps.struct.MsgRequestInfo;

/**
 * components agent ws service
 *
 * @author : sunyuecheng
 */
public interface IComponentsMsgService {

    /**
     * execute
     *
     * @param bornTimestamp  :
     * @param msgRequestInfo :
     * @return boolean :
     */
    boolean execute(long bornTimestamp, MsgRequestInfo msgRequestInfo);
}
