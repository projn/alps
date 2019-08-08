package com.projn.sample.alps.module.rss.msg.request.type;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author : auto
 */
public class AddUserRssInfoRequestInfo {
    /**
     * 用户ID
     */
    @JSONField(name = "user_id")
    private String userId;

    /**
     * 订阅类型
     */
    private String type;

    /**
     * 用户TOKEN
     */
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}