package com.projn.alps.dao.impl;

import com.alibaba.fastjson.JSON;
import com.projn.alps.dao.IAgentMessageInfoDao;
import com.projn.alps.domain.AgentMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;

/**
 * agent message info dao impl
 *
 * @author : sunyuecheng
 */
@Repository("AgentMessageInfoDao")
public class AgentMessageInfoDaoImpl extends SpringDataRedisInfoDaoImpl
        implements IAgentMessageInfoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMessageInfoDaoImpl.class);

    private static final String AGENT_ORDER_MSG_INFO_LIST_HEADER = "a.o.m.i.l:%s";

    private static final String AGENT_COVER_MSG_INFO_MAP_HEADER = "a.c.m.i.m:%s";

    @Override
    public boolean setAgentOrderMessageInfo(AgentMessageInfo agentMessageInfo) {
        if (agentMessageInfo == null || StringUtils.isEmpty(agentMessageInfo.getAgentId())) {
            LOGGER.error("Error param.");
            return false;
        }

        String value = null;
        try {
            value = JSON.toJSONString(agentMessageInfo);
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return false;
        }

        String key = String.format(AGENT_ORDER_MSG_INFO_LIST_HEADER, agentMessageInfo.getAgentId());
        return rpushStrInfoToList(key, value);
    }

    @Override
    public AgentMessageInfo getAgentOrderMessageInfo(String agentId) {
        if (StringUtils.isEmpty(agentId)) {
            LOGGER.error("Error param.");
            return null;
        }

        String key = String.format(AGENT_ORDER_MSG_INFO_LIST_HEADER, agentId);
        String value = rpopStrInfoFromList(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            AgentMessageInfo agentMessageInfo =
                    JSON.parseObject(value, AgentMessageInfo.class);
            return agentMessageInfo;
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return null;
        }
    }

    @Override
    public boolean setAgentCoverMessageInfo(AgentMessageInfo agentMessageInfo) {
        if (agentMessageInfo == null || StringUtils.isEmpty(agentMessageInfo.getAgentId())) {
            LOGGER.error("Error param.");
            return false;
        }

        String value = null;
        try {
            value = JSON.toJSONString(agentMessageInfo);
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return false;
        }
        String key = String.format(AGENT_COVER_MSG_INFO_MAP_HEADER, agentMessageInfo.getAgentId());
        return updateMapItemInfo(key, String.valueOf(agentMessageInfo.getMsgId()), value);
    }

    @Override
    public AgentMessageInfo getAgentCoverMessageInfo(String agentId, Integer msgId) {
        if (StringUtils.isEmpty(agentId) || msgId == null) {
            LOGGER.error("Error param.");
            return null;
        }

        String key = String.format(AGENT_COVER_MSG_INFO_MAP_HEADER, agentId);
        String value = getMapItemInfo(key, msgId.toString());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            AgentMessageInfo agentMessageInfo =
                    JSON.parseObject(value, AgentMessageInfo.class);
            return agentMessageInfo;
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return null;
        }
    }

    @Override
    public boolean deleteAgentCoverMessageInfo(String agentId, Integer msgId) {
        if (StringUtils.isEmpty(agentId) || msgId == null) {
            LOGGER.error("Error param.");
            return false;
        }

        String key = String.format(AGENT_COVER_MSG_INFO_MAP_HEADER, agentId);
        return deleteMapItemInfo(key, msgId.toString());
    }

}
