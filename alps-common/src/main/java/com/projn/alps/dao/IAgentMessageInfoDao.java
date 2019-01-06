package com.projn.alps.dao;

import com.projn.alps.domain.AgentMessageInfo;

/**
 * agent message info dao
 *
 * @author : sunyuecheng
 */
public interface IAgentMessageInfoDao {

    /**
     * set agent order message info
     *
     * @param agentMessageInfo :
     * @return boolean :
     */
    boolean setAgentOrderMessageInfo(AgentMessageInfo agentMessageInfo);

    /**
     * get agent order message info
     *
     * @param agentId :
     * @return AgentMessageInfo :
     */
    AgentMessageInfo getAgentOrderMessageInfo(String agentId);

    /**
     * set agent cover message info
     *
     * @param agentMessageInfo :
     * @return boolean :
     */
    boolean setAgentCoverMessageInfo(AgentMessageInfo agentMessageInfo);

    /**
     * get agent cover message info
     *
     * @param agentId :
     * @param msgId   :
     * @return AgentMessageInfo :
     */
    AgentMessageInfo getAgentCoverMessageInfo(String agentId, Integer msgId);

    /**
     * delete agent cover message info
     *
     * @param agentId :
     * @param msgId   :
     * @return boolean :
     */
    boolean deleteAgentCoverMessageInfo(String agentId, Integer msgId);
}
