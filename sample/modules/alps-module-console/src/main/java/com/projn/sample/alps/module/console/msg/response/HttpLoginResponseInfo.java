package com.projn.sample.alps.module.console.msg.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.projn.sample.alps.module.console.msg.response.type.UserExtendInfoResponseInfo;

/**
 * 
 * @author : auto
 */
public class HttpLoginResponseInfo {
    /**
     * 账户
     */
    private String account;

    /**
     * 用户ID
     */
    @JSONField(name = "user_id")
    private String userId;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像ID
     */
    @JSONField(name = "head_image")
    private String headImage;

    /**
     * 注册时间
     */
    @JSONField(name = "register_time")
    private Long registerTime;

    @JSONField(name = "extend_info")
    private UserExtendInfoResponseInfo extendInfo;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public UserExtendInfoResponseInfo getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(UserExtendInfoResponseInfo extendInfo) {
        this.extendInfo = extendInfo;
    }
}