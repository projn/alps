package com.projn.sample.alps.module.backend.msg.request.type;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author : auto
 */
public class UserMsgInfoRequestInfo {
    /**
     * 用户ID
     */
    @JSONField(name = "user_id")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}