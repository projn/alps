package com.projn.alps.alpsmicroservice.property;

import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * run time properties
 *
 * @author : sunyuecheng
 */
@Component
@RefreshScope
@ConfigurationProperties
@PropertySource(value = {"file:${config.dir}/application.properties"}, ignoreResourceNotFound = true)
public class RunTimeProperties implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunTimeProperties.class);

    @Value("${spring.application.name}")
    private String appName = null;

    @Value("${server.address}")
    private String serverAddress = null;

    @Value("${server.port}")
    private int serverPort = 0;

    @Value("#{systemProperties['os.name']}")
    private String osName = null;

    @Value("${management.server.servlet.context-path}")
    private String actuatorContextPath = null;

    @Value("${websocket.context-path}")
    private String websocketContextPath = null;

    @Value("${logging.config}")
    private String logConfigPath = null;

    @Value("${system.i18n.dir}")
    private String i18nDir = null;

    @Value("${system.waitResponseSeconds}")
    private long waitResponseSeconds = 1;

    @Value("${system.wsSessionTimeOutMinutes}")
    private int wsSessionTimeOutMinutes = MicroServiceDefine.DEFAULT_WEBSOCKET_SESSION_TIMEOUT_INTERVAL_MINUTES;

    @Value("${system.tokenSecretKey}")
    private String tokenSecretKey = null;

    @Value("${system.api.access.role.sendMsg}")
    private String apiAccessRoleSendMsg = null;

    @Value("${system.api.access.role.actuator}")
    private String apiAccessRoleActuator = null;

    @Value("${system.bean.switch.rocketmq:false}")
    private boolean beanSwitchRocketMq = false;

    @Value("${system.bean.switch.websocket:false}")
    private boolean beanSwitchWebsocket = false;

    /**
     * run time properties
     */
    public RunTimeProperties() {

    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getActuatorContextPath() {
        return actuatorContextPath;
    }

    public void setActuatorContextPath(String actuatorContextPath) {
        this.actuatorContextPath = actuatorContextPath;
    }

    public String getWebsocketContextPath() {
        return websocketContextPath;
    }

    public void setWebsocketContextPath(String websocketContextPath) {
        this.websocketContextPath = websocketContextPath;
    }

    public String getI18nDir() {
        return i18nDir;
    }

    public void setI18nDir(String i18nDir) {
        this.i18nDir = i18nDir;
    }

    public long getWaitResponseSeconds() {
        return waitResponseSeconds;
    }

    public void setWaitResponseSeconds(long waitResponseSeconds) {
        this.waitResponseSeconds = waitResponseSeconds;
    }

    public int getWsSessionTimeOutMinutes() {
        return wsSessionTimeOutMinutes;
    }

    public void setWsSessionTimeOutMinutes(int wsSessionTimeOutMinutes) {
        this.wsSessionTimeOutMinutes = wsSessionTimeOutMinutes;
    }

    public String getTokenSecretKey() {
        return tokenSecretKey;
    }

    public void setTokenSecretKey(String tokenSecretKey) {
        this.tokenSecretKey = tokenSecretKey;
    }

    public String getApiAccessRoleSendMsg() {
        return apiAccessRoleSendMsg;
    }

    public void setApiAccessRoleSendMsg(String apiAccessRoleSendMsg) {
        this.apiAccessRoleSendMsg = apiAccessRoleSendMsg;
    }

    public String getLogConfigPath() {
        return logConfigPath;
    }

    public void setLogConfigPath(String logConfigPath) {
        this.logConfigPath = logConfigPath;
    }

    public String getApiAccessRoleActuator() {
        return apiAccessRoleActuator;
    }

    public void setApiAccessRoleActuator(String apiAccessRoleActuator) {
        this.apiAccessRoleActuator = apiAccessRoleActuator;
    }

    public boolean isBeanSwitchRocketMq() {
        return beanSwitchRocketMq;
    }

    public void setBeanSwitchRocketMq(boolean beanSwitchRocketMq) {
        this.beanSwitchRocketMq = beanSwitchRocketMq;
    }

    public boolean isBeanSwitchWebsocket() {
        return beanSwitchWebsocket;
    }

    public void setBeanSwitchWebsocket(boolean beanSwitchWebsocket) {
        this.beanSwitchWebsocket = beanSwitchWebsocket;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.isEmpty(logConfigPath)) {
            Configurator.initialize(null, logConfigPath);
        }
    }
}