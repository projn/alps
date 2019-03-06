package com.projn.alps.tool;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.exception.HttpException;
import com.projn.alps.i18n.LocaleContext;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import com.projn.alps.work.HttpProcessWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.MAX_HTTP_RESPONSE_WAIT_SECONDS;
import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;
import static com.projn.alps.exception.code.CommonErrorCode.*;

/**
 * http controller tools
 *
 * @author : sunyuecheng
 */
@Component
public class HttpControllerTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpControllerTools.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * deal
     *
     * @param url      :
     * @param request      :
     * @param response     :
     * @param pathParamMap :
     * @param requestObj  :
     * @return DeferredResult<Object> :
     * @throws HttpException :
     */
    public DeferredResult<Object> deal(String url, HttpServletRequest request, HttpServletResponse response,
                                       Map<String, String> pathParamMap, Object requestObj) throws HttpException {
        if(StringUtils.isEmpty(url)|| request == null || response == null) {
            LOGGER.error("Invaild param.");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVALID_REQUEST_INFO_ERROR);
        }

        final DeferredResult<Object> deferredResult = new DeferredResult<Object>(
                MAX_HTTP_RESPONSE_WAIT_SECONDS * MILLI_SECOND_1000);

        LOGGER.info("Request url({}).", request.getRequestURI());

        String method = request.getMethod().toLowerCase();
        Map<String, RequestServiceInfo> requestServiceInfoMap
                = ServiceData.getRequestServiceInfoMap().get(url);

        if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()
                || requestServiceInfoMap.get(method) == null) {
            LOGGER.error("Invaild request service info, uri({}), method({}).",
                    request.getRequestURI(), request.getMethod());
            throw new HttpException(HttpStatus.NOT_FOUND.value(), RESULT_INVALID_REQUEST_INFO_ERROR);
        }
        RequestServiceInfo requestServiceInfo = requestServiceInfoMap.get(method);
        if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_HTTP)) {
            LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_INVALID_REQUEST_INFO_ERROR);
        }

        if (requestServiceInfo.getAuthorizationFilter() != null) {
            try {
                requestServiceInfo.getAuthorizationFilter().auth(request, requestServiceInfo.getUserRoleNameList());
            } catch (Exception e) {
                LOGGER.error("Check authorization info error,error info(" + e.getMessage() + ").");
                throw new HttpException(HttpStatus.UNAUTHORIZED.value(), RESULT_INVAILD_USER_TOKEN_ERROR);
            }
        }

        HttpRequestInfo httpRequestInfo = null;
        if (requestServiceInfo.getParamClass() != null) {
            try {
                if(requestObj instanceof JSONObject) {
                    httpRequestInfo = RequestInfoUtils.convertHttpRequestInfo(request, pathParamMap,
                            (JSONObject)requestObj, null, requestServiceInfo.getParamClass());
                } else if(requestObj instanceof MultipartFile) {
                    httpRequestInfo = RequestInfoUtils.convertHttpRequestInfo(request, pathParamMap,
                            null, (MultipartFile)requestObj, requestServiceInfo.getParamClass());
                } else {
                    httpRequestInfo = RequestInfoUtils.convertHttpRequestInfo(request, pathParamMap,
                            null, null, requestServiceInfo.getParamClass());
                }
            } catch (Exception e) {
                LOGGER.error("Convert request info error,error info(" + e.getMessage() + ").");
                throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_ANALYSE_REQUEST_ERROR);
            }

            if (httpRequestInfo != null && httpRequestInfo.getParamObj() != null) {
                try {
                    ParamCheckUtils.checkParam(httpRequestInfo.getParamObj());
                } catch (Exception e) {
                    LOGGER.error("Check param error,error info(" + e.getMessage() + ").");
                    throw new HttpException(HttpStatus.BAD_REQUEST.value(), RESULT_ANALYSE_REQUEST_ERROR);
                }
            }
        }

        if(httpRequestInfo!= null) {
            LocaleContext.set(httpRequestInfo.getLocale());
        }

        if (taskExecutor.getActiveCount() < taskExecutor.getMaxPoolSize()) {
            taskExecutor.execute(new HttpProcessWorker(requestServiceInfo.getServiceName(),
                    httpRequestInfo, response, deferredResult));
        } else {
            LOGGER.debug("System is busy.");
            throw new HttpException(HttpStatus.TOO_MANY_REQUESTS.value(), RESULT_SYSTEM_IS_BUSY_ERROR);
        }

        return deferredResult;
    }

}
