package com.projn.sample.alps.module.console.msg.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;

/**
 * @author : auto
 */
public class HttpLogoutRequestInfo {
    /**
     * JWT
     */
    @JSONField(name = "x-access-token")
    @ParamLocation(location = ParamLocationType.HEADER)
    private String xAccessToken;

    /**
     * 用户ID
     */
    @JSONField(name = "user_id")
    @ParamLocation(location = ParamLocationType.PATH)
    private String userId;

    public String getxAccessToken() {
        return xAccessToken;
    }

    public void setxAccessToken(String xAccessToken) {
        this.xAccessToken = xAccessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}