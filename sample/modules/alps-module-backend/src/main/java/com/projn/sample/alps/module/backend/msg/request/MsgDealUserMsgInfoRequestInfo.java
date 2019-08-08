package com.projn.sample.alps.module.backend.msg.request;

import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;
import com.projn.sample.alps.module.backend.msg.request.type.UserMsgInfoRequestInfo;

/**
 * @author : auto
 */
public class MsgDealUserMsgInfoRequestInfo {
    @ParamLocation(location = ParamLocationType.BODY)
    private UserMsgInfoRequestInfo userMsgInfoRequestInfo;

    public UserMsgInfoRequestInfo getUserMsgInfoRequestInfo() {
        return userMsgInfoRequestInfo;
    }

    public void setUserMsgInfoRequestInfo(UserMsgInfoRequestInfo userMsgInfoRequestInfo) {
        this.userMsgInfoRequestInfo = userMsgInfoRequestInfo;
    }
}