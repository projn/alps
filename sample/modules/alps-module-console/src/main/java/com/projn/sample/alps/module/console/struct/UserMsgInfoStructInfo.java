package com.projn.sample.alps.module.console.struct;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author : auto
 */
public class UserMsgInfoStructInfo {
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