package com.projn.sample.alps.module.console.msg.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : auto
 */
public class HttpUploadUserHeadImageInfoRequestInfo {
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

    @ParamLocation(location = ParamLocationType.BODY)
    private MultipartFile file;

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

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }
}