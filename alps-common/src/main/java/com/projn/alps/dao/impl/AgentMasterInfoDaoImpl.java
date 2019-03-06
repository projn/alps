package com.projn.alps.dao.impl;

import com.alibaba.fastjson.JSON;
import com.projn.alps.dao.IAgentMasterInfoDao;
import com.projn.alps.domain.AgentMasterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;

/**
 * agent server info dao impl
 *
 * @author : sunyuecheng
 */
@Repository("AgentMasterInfoDao")
public class AgentMasterInfoDaoImpl extends SpringDataRedisInfoDaoImpl
        implements IAgentMasterInfoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMasterInfoDaoImpl.class);

    private static final String AGENT_MASTER_INRO_KEY = "a.m";

    @Override
    public boolean setAgentMasterInfo(AgentMasterInfo agentMasterInfo) {
        if (agentMasterInfo == null || StringUtils.isEmpty(agentMasterInfo.getAgentId())) {
            LOGGER.error("Error param.");
            return false;
        }

        String value = null;
        try {
            value = JSON.toJSONString(agentMasterInfo);
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return false;
        }

        updateMapItemInfo(AGENT_MASTER_INRO_KEY, agentMasterInfo.getAgentId(), value);
        return true;
    }

    @Override
    public AgentMasterInfo getAgentMasterInfo(String agentId) {
        if (StringUtils.isEmpty(agentId)) {
            LOGGER.error("Error param.");
            return null;
        }

        String value = getMapItemInfo(AGENT_MASTER_INRO_KEY, agentId);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            AgentMasterInfo agentMasterInfo =
                    JSON.parseObject(value, AgentMasterInfo.class);
            return agentMasterInfo;
        } catch (Exception e) {
            LOGGER.error("Convert object error,error info(" + e.getMessage() + ").");
            return null;
        }
    }

    @Override
    public boolean deleteAgentMasterInfo(String agentId) {
        if (StringUtils.isEmpty(agentId)) {
            LOGGER.error("Error param.");
            return false;
        }
        return deleteMapItemInfo(AGENT_MASTER_INRO_KEY, agentId);
    }
}
