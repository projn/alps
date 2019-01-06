package com.projn.alps.struct;

/**
 * master info
 *
 * @author : sunyuecheng
 */
public class MasterInfo {

    /**
     * role name
     */
    private String roleName;

    /**
     * server ip
     */
    private String serverIp;

    /**
     * server port
     */
    private Integer serverPort;

    /**
     * master info
     */
    public MasterInfo() {
    }

    /**
     * master info
     *
     * @param roleName   :
     * @param serverIp   :
     * @param serverPort :
     */
    public MasterInfo(String roleName, String serverIp, Integer serverPort) {
        this.roleName = roleName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
}