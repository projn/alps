package com.projn.alps.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.exception.HttpException;
import com.projn.alps.i18n.LocaleContext;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import com.projn.alps.work.HttpProcessWorker;
import com.projn.alps.work.MsgProcessWorker;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.Future;

import static com.projn.alps.define.CommonDefine.MAX_HTTP_RESPONSE_WAIT_SECONDS;
import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;
import static com.projn.alps.exception.code.CommonErrorCode.*;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * http controller tools
 *
 * @author : sunyuecheng
 */
@Component
public class MsgControllerTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(MsgControllerTools.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * deal
     *
     * @param msg          :
     * @param acknowledgment      :
     */
    public void deal(ConsumerRecord<String, String> msg, Acknowledgment acknowledgment) {
        if (msg == null ) {
            LOGGER.error("Invaild param.");
            if(acknowledgment!=null) {
                acknowledgment.acknowledge();
            }
            return;
        }

        MsgRequestInfo msgRequestInfo = null;
        try {
            String body = msg.value();
            msgRequestInfo = (MsgRequestInfo) JSONObject.parseObject(body, MsgRequestInfo.class);
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info({}).", formatExceptionInfo(e));
            if(acknowledgment!=null) {
                acknowledgment.acknowledge();
            }
            return;
        }
        if (msgRequestInfo == null) {
            if(acknowledgment!=null) {
                acknowledgment.acknowledge();
            }
            return;
        }

        String uri = msg.topic() + "/" + msgRequestInfo.getId();

        LOGGER.info("Request url({}).", uri);

        Map<String, RequestServiceInfo> requestServiceInfoMap = ServiceData.getRequestServiceInfoMap().get(uri);
        if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()) {
            LOGGER.error("Invaild request service info, msg id (" + msgRequestInfo.getId() + ").");

            if(acknowledgment!=null) {
                acknowledgment.acknowledge();
            }
            return;
        }

        for (Map.Entry<String, RequestServiceInfo> item : requestServiceInfoMap.entrySet()) {
            RequestServiceInfo requestServiceInfo = item.getValue();
            if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_MSG)) {
                LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
                continue;
            }

            MsgRequestInfo targetMsgRequestInfo = null;
            if (requestServiceInfo.getParamClass() != null) {

                try {
                    String msgText = JSON.toJSONString(msgRequestInfo.getMsg());
                    targetMsgRequestInfo = RequestInfoUtils.convertMsgRequestInfo(
                            msgText, requestServiceInfo.getParamClass());
                } catch (Exception e) {
                    LOGGER.error("Convert request info error,error info(" + e.getMessage() + ").");
                    continue;
                }

                if (targetMsgRequestInfo != null && targetMsgRequestInfo.getMsg() != null) {
                    try {
                        ParamCheckUtils.checkParam(targetMsgRequestInfo.getMsg());
                    } catch (Exception e) {
                        LOGGER.error("Check param error,error info(" + e.getMessage() + ").");
                        continue;
                    }
                }

                if (targetMsgRequestInfo != null) {
                    targetMsgRequestInfo.setId(msgRequestInfo.getId());
                    targetMsgRequestInfo.setExtendInfoMap(msgRequestInfo.getExtendInfoMap());
                }
            }

            try {
                if (taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
                    Future<?> future = taskExecutor.submit(
                            new MsgProcessWorker(requestServiceInfo.getServiceName(),
                                    targetMsgRequestInfo, msg.timestamp()));
                    future.isDone();
                } else {
                    LOGGER.debug("System is busy.");
                    return;
                }
            } catch (Exception e) {
                LOGGER.error("Analyse request info error,msg info({}),error info({}).",
                        JSON.toJSONString(msg), formatExceptionInfo(e));
                return;
            }
        }
        if(acknowledgment!=null) {
            acknowledgment.acknowledge();
        }
    }

}
