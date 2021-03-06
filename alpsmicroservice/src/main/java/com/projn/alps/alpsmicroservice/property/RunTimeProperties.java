package com.projn.alps.alpsmicroservice.property;

import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

    @Value("${spring.application.name}")
    private String appName = null;

    @Value("${server.address}")
    private String serverAddress = null;

    @Value("${server.port}")
    private int serverPort = 0;

    @Value("${server.ssl:false}")
    private boolean serverSsl = false;

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

    @Value("${system.wsSessionTimeOutMinutes}")
    private int wsSessionTimeOutMinutes = MicroServiceDefine.DEFAULT_WEBSOCKET_SESSION_TIMEOUT_INTERVAL_MINUTES;

    @Value("${system.maxWsSessionCount}")
    private int maxWsSessionCount = MicroServiceDefine.DEFAULT_MAX_WEBSOCKET_SESSION_COUNT;

    @Value("${system.tokenSecretKey}")
    private String tokenSecretKey = null;

    @Value("${system.api.access.role.sendMsg}")
    private String apiAccessRoleSendMsg = null;

    @Value("${system.api.access.role.actuator}")
    private String apiAccessRoleActuator = null;

    @Value("${system.bean.switch.mq.consumer:false}")
    private boolean beanSwitchMqConsumer = false;

    @Value("${system.bean.switch.mq.producer:false}")
    private boolean beanSwitchMqProducer = false;

    @Value("${system.bean.switch.websocket:false}")
    private boolean beanSwitchWebsocket = false;

    @Value("${system.ws.msg.ids}")
    private String wsMsgIds = null;

    private List<String> wsMsgIdList = null;

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

    public boolean isServerSsl() {
        return serverSsl;
    }

    public void setServerSsl(boolean serverSsl) {
        this.serverSsl = serverSsl;
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

    public int getWsSessionTimeOutMinutes() {
        return wsSessionTimeOutMinutes;
    }

    public void setWsSessionTimeOutMinutes(int wsSessionTimeOutMinutes) {
        this.wsSessionTimeOutMinutes = wsSessionTimeOutMinutes;
    }

    public int getMaxWsSessionCount() {
        return maxWsSessionCount;
    }

    public void setMaxWsSessionCount(int maxWsSessionCount) {
        this.maxWsSessionCount = maxWsSessionCount;
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

    public boolean isBeanSwitchMqConsumer() {
        return beanSwitchMqConsumer;
    }

    public void setBeanSwitchMqConsumer(boolean beanSwitchMqConsumer) {
        this.beanSwitchMqConsumer = beanSwitchMqConsumer;
    }

    public boolean isBeanSwitchMqProducer() {
        return beanSwitchMqProducer;
    }

    public void setBeanSwitchMqProducer(boolean beanSwitchMqProducer) {
        this.beanSwitchMqProducer = beanSwitchMqProducer;
    }

    public boolean isBeanSwitchWebsocket() {
        return beanSwitchWebsocket;
    }

    public void setBeanSwitchWebsocket(boolean beanSwitchWebsocket) {
        this.beanSwitchWebsocket = beanSwitchWebsocket;
    }

    public String getWsMsgIds() {
        return wsMsgIds;
    }

    public void setWsMsgIds(String wsMsgIds) {
        this.wsMsgIds = wsMsgIds;
    }

    public List<String> getWsMsgIdList() {
        return wsMsgIdList;
    }

    public void setWsMsgIdList(List<String> wsMsgIdList) {
        this.wsMsgIdList = wsMsgIdList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.isEmpty(logConfigPath)) {
            Configurator.initialize(null, logConfigPath);
        }

        if (!StringUtils.isEmpty(wsMsgIds)) {
            wsMsgIdList = Arrays.asList(wsMsgIds.split(","));
        }
    }
}
