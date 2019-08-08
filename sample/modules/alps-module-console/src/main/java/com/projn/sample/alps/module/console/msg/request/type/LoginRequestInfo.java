package com.projn.sample.alps.module.console.msg.request.type;

import com.alibaba.fastjson.annotation.JSONField;
import com.projn.alps.msg.filter.ParamLimit;

/**
 * @author : auto
 */
public class LoginRequestInfo {
    /**
     * 账户
     */
    @ParamLimit(nullable = true, maxLength = 64)
    private String account;

    /**
     * 密码md5
     */
    @ParamLimit(nullable = true, maxLength = 64)
    private String key;

    /**
     * 设备ID
     */
    @JSONField(name = "device_id")
    private String deviceId;

    /**
     * 验证码
     */
    @JSONField(name = "verification_code")
    @ParamLimit(nullable = true, minLength = 4)
    private String verificationCode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}