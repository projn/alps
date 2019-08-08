package com.projn.sample.alps.module.console.msg.request;

import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;
import com.projn.sample.alps.module.console.msg.request.type.LoginRequestInfo;

/**
 * @author : auto
 */
public class HttpLoginRequestInfo {
    @ParamLocation(location = ParamLocationType.BODY)
    private LoginRequestInfo loginRequestInfo;

    public LoginRequestInfo getLoginRequestInfo() {
        return loginRequestInfo;
    }

    public void setLoginRequestInfo(LoginRequestInfo loginRequestInfo) {
        this.loginRequestInfo = loginRequestInfo;
    }
}