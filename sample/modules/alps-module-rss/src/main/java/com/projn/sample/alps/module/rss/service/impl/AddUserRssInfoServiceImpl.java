package com.projn.sample.alps.module.rss.service.impl;

import com.alibaba.fastjson.JSON;
import com.projn.alps.service.IComponentsWsService;
import com.projn.alps.struct.WsRequestInfo;
import com.projn.alps.struct.WsResponseInfo;
import com.projn.sample.alps.module.rss.msg.request.WsAddUserRssInfoRequestInfo;
import com.projn.sample.alps.module.rss.msg.request.type.AddUserRssInfoRequestInfo;
import com.projn.sample.alps.module.rss.msg.response.WsAddUserRssInfoResponseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加用户订阅
 *
 * @author : auto
 */
@Component("AddUserRssInfoServiceImpl")
public class AddUserRssInfoServiceImpl implements IComponentsWsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddUserRssInfoServiceImpl.class);

    @Override
    public WsResponseInfo execute(WsRequestInfo wsRequestInfo) {
        WsResponseInfo wsResponseInfo = new WsResponseInfo();

        if (wsRequestInfo == null || wsRequestInfo.getParamObj() == null) {
            LOGGER.error("Error param.");
            return null;
        }

        //Do not check request param,use the annotation '@ParamLimit'
        WsAddUserRssInfoRequestInfo wsAddUserRssInfoRequestInfo
                = (WsAddUserRssInfoRequestInfo) wsRequestInfo.getParamObj();
        AddUserRssInfoRequestInfo addUserRssInfoRequestInfo
                = wsAddUserRssInfoRequestInfo.getAddUserRssInfoRequestInfo();

        LOGGER.info("Msg({}).", JSON.toJSONString(addUserRssInfoRequestInfo));

        WsAddUserRssInfoResponseInfo wsAddUserRssInfoResponseInfo = new WsAddUserRssInfoResponseInfo();
        wsAddUserRssInfoResponseInfo.setStatus("OK");
        wsResponseInfo.setMsg(wsAddUserRssInfoResponseInfo);

        //Add extend data to websocket session
        Map<String, Object> extendData = new HashMap<>();
        extendData.put("ID", "id");
        wsResponseInfo.setExtendInfoMap(extendData);

        return wsResponseInfo;
    }
}