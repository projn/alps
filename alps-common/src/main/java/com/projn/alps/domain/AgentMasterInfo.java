package com.projn.alps.domain;


/**
 * agent master info
 *
 * @author : sunyuecheng
 */
public class AgentMasterInfo {
    private String agentId;
    private String serverIp;
    private Integer serverPort;
    private String apiUrl;

    /**
     * agent master info
     */
    public AgentMasterInfo() {
    }


    /**
     * agent master info
     *
     * @param agentId    :
     * @param serverIp   :
     * @param serverPort :
     * @param apiUrl     :
     */
    public AgentMasterInfo(String agentId, String serverIp, Integer serverPort, String apiUrl) {
        this.agentId = agentId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
