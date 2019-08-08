package com.projn.sample.alps.module.console.service.impl;

import com.projn.alps.exception.HttpException;
import com.projn.alps.exception.code.CommonErrorCode;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;
import com.projn.alps.util.JwtTokenUtils;
import com.projn.sample.alps.module.console.dao.IUserInfoDao;
import com.projn.sample.alps.module.console.domain.UserInfo;
import com.projn.sample.alps.module.console.msg.request.HttpLoginRequestInfo;
import com.projn.sample.alps.module.console.msg.request.type.LoginRequestInfo;
import com.projn.sample.alps.module.console.msg.response.HttpLoginResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.ONE_YEAR_SECOND;

/**
 * 用户登录
 *
 * @author : auto
 */
@Component("LoginServiceImpl")
public class LoginServiceImpl implements IComponentsHttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private IUserInfoDao userInfoDao;

    public void init() {
        LOGGER.info("Init method.");
    }

    @Override
    public HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException {
        HttpResponseInfo httpResponseInfo = new HttpResponseInfo();

        if (httpRequestInfo == null || httpRequestInfo.getParamObj() == null) {
            LOGGER.error("Error param.");
            throw new HttpException(HttpStatus.BAD_REQUEST.value(), CommonErrorCode.RESULT_INVAILD_PARAM_ERROR);
        }

        //Do not check request param,use the annotation '@ParamLimit'
        HttpLoginRequestInfo httpLoginRequestInfo = (HttpLoginRequestInfo) httpRequestInfo.getParamObj();
        LoginRequestInfo loginRequestInfo = httpLoginRequestInfo.getLoginRequestInfo();

        HttpLoginResponseInfo httpLoginResponseInfo = new HttpLoginResponseInfo();

        UserInfo userInfo = null;
        try {
            userInfo = userInfoDao.selectByPrimaryKey(loginRequestInfo.getAccount());
        } catch (Exception e) {
            LOGGER.error("Get user info by account,account({}).", loginRequestInfo.getAccount());
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(), CommonErrorCode.RESULT_SQL_ERROR);
        }

        httpLoginResponseInfo.setAccount(loginRequestInfo.getAccount());
        httpLoginResponseInfo.setUserId(userInfo.getUserId());
        httpLoginResponseInfo.setName(userInfo.getName());
        httpLoginResponseInfo.setHeadImage(userInfo.getHeadImage());
        httpLoginResponseInfo.setRegisterTime(userInfo.getRegisterTime());

        String jwtToken = null;
        try {
            Map<String, Object> claimMap = new HashMap<>();

            claimMap.put("ID", userInfo.getUserId());
            claimMap.put("NAME", userInfo.getName());
            claimMap.put("ROLE", "admin");

            jwtToken = JwtTokenUtils.createToken("test", claimMap, "projn", "Server",
                    ONE_YEAR_SECOND, "tokenKey");
        } catch (Exception e) {
            LOGGER.error("Create jwt token error,error info(" + e.getMessage() + ").");
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(), CommonErrorCode.RESULT_SYSTEM_INTER_ERROR);
        }

        Map<String, String> headerInfoMap = new HashMap<>();
        headerInfoMap.put("x-access-token", jwtToken);
        httpResponseInfo.setHeaderInfoMap(headerInfoMap);

        return httpResponseInfo;
    }
}