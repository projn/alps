package com.projn.alps.work;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.projn.alps.define.HttpDefine;
import com.projn.alps.exception.HttpException;
import com.projn.alps.i18n.LocaleContext;
import com.projn.alps.initialize.InitializeBean;
import com.projn.alps.msg.response.HttpErrorResponseMsgInfo;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.alps.util.CounterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static com.projn.alps.exception.code.CommonErrorCode.*;
import static com.projn.alps.define.CommonDefine.MSG_RESPONSE_MAX_TIME_HEADER;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;


/**
 * http process worker
 *
 * @author : sunyuecheng
 */
public class HttpProcessWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProcessWorker.class);

    private String serviceName = null;
    private HttpRequestInfo httpRequestInfo = null;

    private HttpServletResponse response;
    private DeferredResult<Object> deferredResult;

    /**
     * http process worker
     *
     * @param serviceName     :
     * @param httpRequestInfo :
     * @param response        :
     * @param deferredResult  :
     */
    public HttpProcessWorker(String serviceName, HttpRequestInfo httpRequestInfo,
                             HttpServletResponse response, DeferredResult<Object> deferredResult) {
        this.serviceName = serviceName;
        this.httpRequestInfo = httpRequestInfo;

        this.response = response;
        this.deferredResult = deferredResult;
    }

    /**
     * run
     */
    @Override
    public void run() {
        LocaleContext.set(httpRequestInfo.getLocale());
        response.setContentType(HttpDefine.CONTENT_TYPE_APPLICATION_JSON_UTF_8);

        HttpErrorResponseMsgInfo httpErrorResponseMsgInfo = null;
        HttpResponseInfo httpResponseInfo = null;
        try {
            IComponentsHttpService bean = InitializeBean.getBean(serviceName);
            if (bean == null) {
                LOGGER.error("Invaild request url error,request info({}).",
                        JSON.toJSONString(httpRequestInfo));

                response.setStatus(HttpStatus.NOT_FOUND.value());
                throw new HttpException(HttpStatus.NOT_FOUND.value(), RESULT_INVALID_REQUEST_INFO_ERROR);
            } else {
                long start = System.currentTimeMillis();

                httpResponseInfo = bean.execute(httpRequestInfo);

                long end = System.currentTimeMillis();
                CounterUtils.recordMaxNum(String.format(MSG_RESPONSE_MAX_TIME_HEADER,
                        serviceName),
                        (double) (end - start));
            }
        } catch (HttpException e) {
            response.setStatus(e.getHttpStatus());
            httpErrorResponseMsgInfo = new HttpErrorResponseMsgInfo(e.getErrorCode(), e.getErrorDescription());
        } catch (Exception e) {
            LOGGER.error("Deal request info error ,error info({}).", formatExceptionInfo(e));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpErrorResponseMsgInfo = new HttpErrorResponseMsgInfo(RESULT_SYSTEM_INTER_ERROR.getErrorCode(),
                    RESULT_SYSTEM_INTER_ERROR.getMessage());
        }

        if (httpResponseInfo == null) {
            deferredResult.setResult(httpErrorResponseMsgInfo);
        } else {
            if (httpResponseInfo.getHeaderInfoMap() != null) {
                for (Map.Entry<String, String> entry : httpResponseInfo.getHeaderInfoMap().entrySet()) {
                    response.setHeader(entry.getKey(), entry.getValue());
                }
            }

            if (httpResponseInfo.getMsg() == null) {
                httpResponseInfo.setMsg(new HttpErrorResponseMsgInfo(RESULT_OK.getErrorCode(), null));
            }
            if(httpResponseInfo.getMsg().getClass() ==(byte[].class)) {
                try {
                    response.getOutputStream().write((byte[]) httpResponseInfo.getMsg());
                    response.getOutputStream().flush();
                } catch (Exception e) {
                    LOGGER.error("Write response info error ,error info({}).", formatExceptionInfo(e));
                }
                deferredResult.setResult(null);
            } else {
                deferredResult.setResult(httpResponseInfo.getMsg());
            }
        }

        LocaleContext.remove();
        return;
    }
}
