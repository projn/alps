package com.projn.sample.alps.module.console.service.impl;

import com.projn.alps.exception.HttpException;
import com.projn.alps.exception.code.CommonErrorCode;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 用户注销登录
 *
 * @author : auto
 */
@Component("LogoutServiceImpl")
public class LogoutServiceImpl implements IComponentsHttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServiceImpl.class);

    @Override
    public HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException {
        HttpResponseInfo httpResponseInfo = new HttpResponseInfo();

        if (httpRequestInfo == null || httpRequestInfo.getParamObj() == null) {
            LOGGER.error("Error param.");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), CommonErrorCode.RESULT_INVAILD_PARAM_ERROR);
        }

        return httpResponseInfo;
    }
}