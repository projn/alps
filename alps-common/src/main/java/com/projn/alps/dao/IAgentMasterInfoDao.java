package com.projn.alps.dao;

import com.projn.alps.domain.AgentMasterInfo;

/**
 * agent master info dao
 *
 * @author : sunyuecheng
 */
public interface IAgentMasterInfoDao {

    /**
     * set agent master info
     *
     * @param agentMasterInfo :
     * @return boolean :
     */
    boolean setAgentMasterInfo(AgentMasterInfo agentMasterInfo);

    /**
     * get agent master info
     *
     * @param agentId :
     * @return AgentMasterInfo :
     */
    AgentMasterInfo getAgentMasterInfo(String agentId);

    /**
     * delete agent master info
     *
     * @param agentId :
     * @return boolean :
     */
    boolean deleteAgentMasterInfo(String agentId);

}
