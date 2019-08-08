package com.projn.sample.alps.module.rss.msg.request;

import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;
import com.projn.sample.alps.module.rss.msg.request.type.AddUserRssInfoRequestInfo;

/**
 * @author : auto
 */
public class WsAddUserRssInfoRequestInfo {
    @ParamLocation(location = ParamLocationType.BODY)
    private AddUserRssInfoRequestInfo addUserRssInfoRequestInfo;

    public AddUserRssInfoRequestInfo getAddUserRssInfoRequestInfo() {
        return addUserRssInfoRequestInfo;
    }

    public void setAddUserRssInfoRequestInfo(AddUserRssInfoRequestInfo addUserRssInfoRequestInfo) {
        this.addUserRssInfoRequestInfo = addUserRssInfoRequestInfo;
    }
}