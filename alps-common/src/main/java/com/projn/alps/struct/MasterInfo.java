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
     * server ssl
     */
    private boolean serverSsl = false;

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
     * @param serverSsl  :
     */
    public MasterInfo(String roleName, String serverIp, Integer serverPort, boolean serverSsl) {
        this.roleName = roleName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.serverSsl = serverSsl;
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

    public boolean isServerSsl() {
        return serverSsl;
    }

    public void setServerSsl(boolean serverSsl) {
        this.serverSsl = serverSsl;
    }
}