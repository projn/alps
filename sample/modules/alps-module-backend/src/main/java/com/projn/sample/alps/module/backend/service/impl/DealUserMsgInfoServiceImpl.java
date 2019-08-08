package com.projn.sample.alps.module.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.projn.alps.service.IComponentsMsgService;
import com.projn.alps.struct.MsgRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 处理一般消息
 *
 * @author : auto
 */
@Component("DealUserMsgInfoServiceImpl")
public class DealUserMsgInfoServiceImpl implements IComponentsMsgService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DealUserMsgInfoServiceImpl.class);

    @Override
    public boolean execute(long bornTimestamp, MsgRequestInfo msgRequestInfo) {

        LOGGER.info(JSON.toJSONString(msgRequestInfo));
        return true;
    }
}