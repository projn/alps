package com.projn.alps.alpsmicroservice.listener;

import com.projn.alps.alpsmicroservice.aop.HttpServiceMonitorAop;
import com.projn.alps.alpsmicroservice.aop.MsgServiceMonitorAop;
import com.projn.alps.alpsmicroservice.aop.WsServiceMonitorAop;
import com.projn.alps.alpsmicroservice.config.SpringKafkaConsumerConfig;
import com.projn.alps.alpsmicroservice.job.RemoveInvaildWsSessionInfoJob;
import com.projn.alps.alpsmicroservice.job.SendAgentMsgJob;
import com.projn.alps.alpsmicroservice.mq.KafkaAckConsumerListener;
import com.projn.alps.alpsmicroservice.mq.KafkaBatchConsumerListener;
import com.projn.alps.alpsmicroservice.mq.KafkaConsumerListener;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.alpsmicroservice.struct.ModuleInfo;
import com.projn.alps.alpsmicroservice.struct.ModuleJobInfo;
import com.projn.alps.aop.IHttpServiceMonitorAop;
import com.projn.alps.aop.IMsgServiceMonitorAop;
import com.projn.alps.aop.IWsServiceMonitorAop;
import com.projn.alps.filter.IAuthorizationFilter;
import com.projn.alps.filter.impl.HttpJwtAuthFilterImpl;
import com.projn.alps.i18n.MessageContext;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.msg.request.HttpSendMsgRequestMsgInfo;
import com.projn.alps.struct.MasterInfo;
import com.projn.alps.struct.MqConsumerInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.tool.QuartzJobTools;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static com.projn.alps.alpsmicroservice.define.MicroServiceDefine.HTTP_API_JOB_CRON_EXPRESSION;
import static com.projn.alps.define.CommonDefine.*;
import static com.projn.alps.define.HttpDefine.HTTP_METHOD_POST;
import static com.projn.alps.struct.RequestServiceInfo.SERVICE_TYPE_HTTP;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * system initialize context listener
 *
 * @author : sunyuecheng
 */
public final class SystemInitializeContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemInitializeContextListener.class);

    private static final int MSG_URI_PART_NUM = 2;

    private RunTimeProperties runTimeProperties;

    private SpringKafkaConsumerConfig springKafkaConsumerConfig;

    private ConsumerFactory<String, String> consumerFactory;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private List<ModuleInfo> moduleInfoList;

    private static final int MODULE_INFO_ITEM_SIZE = 2;
    private static final String MODULE_JAR_FILE_TAIL = ".jar";
    private static final String MODULE_CONFIG_FILE_NAME = "module_config.xml";
    private static final String XML_ELEMENT_BASE_PACKAGE = "base_package";
    private static final String XML_ELEMENT_PROPERTIES = "properties";
    private static final String XML_ELEMENT_SERVICES = "services";
    private static final String XML_ELEMENT_JOBS = "jobs";
    private static final String XML_ELEMENT_NAME = "name";
    private static final String XML_ELEMENT_URI = "uri";
    private static final String XML_ELEMENT_TYPE = "type";
    private static final String XML_ELEMENT_ENABLE = "enable";
    private static final String XML_ELEMENT_METHOD = "method";
    private static final String XML_ELEMENT_INIT_METHOD = "init_method";
    private static final String XML_ELEMENT_AUTH_FILTER_NAME = "auth_filter_name";
    private static final String XML_ELEMENT_USER_ROLE_NAME = "user_role_name";
    private static final String XML_ELEMENT_PARAM_BEAN_CLASS_NAME = "param_bean_class_name";
    private static final String XML_ELEMENT_CLASS_NAME = "class_name";
    private static final String XML_ELEMENT_CRON_EXPRESSION = "cron_expression";

    /**
     * system initialize context listener
     *
     * @param configPath :
     * @throws Exception :
     */
    public SystemInitializeContextListener(String configPath) throws Exception {
        loadModuleInfo(configPath);
    }

    /**
     * on application event
     *
     * @param contextRefreshedEvent :
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            runTimeProperties =
                    (RunTimeProperties) contextRefreshedEvent.getApplicationContext().getBean("runTimeProperties");

            if (runTimeProperties.isBeanSwitchMqConsumer()) {
                springKafkaConsumerConfig = (SpringKafkaConsumerConfig)
                        contextRefreshedEvent.getApplicationContext().getBean("springKafkaConsumerConfig");
                consumerFactory = (ConsumerFactory<String, String>)
                        contextRefreshedEvent.getApplicationContext().getBean("consumerFactory");
            }

            threadPoolTaskExecutor = (ThreadPoolTaskExecutor) contextRefreshedEvent.getApplicationContext().
                    getBean("threadPoolTaskExecutor");

            ServiceData.setMasterInfo(
                    new MasterInfo(runTimeProperties.getAppName(), runTimeProperties.getServerAddress(),
                            runTimeProperties.getServerPort(), runTimeProperties.isServerSsl()));

            ServiceData.setJwtSecretKey(runTimeProperties.getTokenSecretKey());

        } catch (Exception e) {
            LOGGER.error("Get context info error,error info(" + formatExceptionInfo(e) + ").");
            return;
        }

        if (!MessageContext.loadModuleLocalKeyMessage(runTimeProperties.getI18nDir())) {
            return;
        }

        loadServiceMonitorAopInfo(contextRefreshedEvent.getApplicationContext());

        Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap = null;
        try {
            requestServiceInfoMap = loadModuleConfigInfo(contextRefreshedEvent.getApplicationContext());
            ServiceData.setRequestServiceInfoMap(requestServiceInfoMap);
        } catch (Exception e) {
            LOGGER.error("Load module info error,error info({}).", formatExceptionInfo(e));
            return;
        }

        if (runTimeProperties.isBeanSwitchMqConsumer()) {
            Map<String, MqConsumerInfo> mqConsumerInfoMap = null;
            try {
                mqConsumerInfoMap = initializeMq(requestServiceInfoMap);
                ServiceData.setMqConsumerInfoMap(mqConsumerInfoMap);
            } catch (Exception e) {
                LOGGER.error("Initialize rocket mq error,error info({}).", formatExceptionInfo(e));
                return;
            }
        }
    }

    private void loadModuleInfo(String modulesDir) throws Exception {
        moduleInfoList = new ArrayList<>();

        File modulesDirFile = new File(modulesDir);
        if (!modulesDirFile.isDirectory() || !modulesDirFile.exists()) {
            LOGGER.info("Invaild modules dir, dir({}).", modulesDir);
            return;
        }

        File[] moduleDirFileList = modulesDirFile.listFiles();
        if (moduleDirFileList == null || moduleDirFileList.length == 0) {
            throw new Exception("Invaild module info.");
        }

        for (int i = 0; i < moduleDirFileList.length; i++) {
            File moduleDirFile = moduleDirFileList[i];
            if (!moduleDirFile.isDirectory()) {
                continue;
            }

            File[] moduleInfoFileList = moduleDirFile.listFiles();
            if (moduleInfoFileList == null || moduleInfoFileList.length < MODULE_INFO_ITEM_SIZE) {
                continue;
            }

            List<String> moduleJarPathList = new ArrayList<>();
            String moduleConfigPath = null;

            for (int j = 0; j < moduleInfoFileList.length; j++) {
                File moduleInfoFile = moduleInfoFileList[j];
                if (moduleInfoFile.isDirectory()) {
                    continue;
                }

                if (moduleInfoFile.getName().endsWith(MODULE_JAR_FILE_TAIL)) {
                    String jarPath = "file:" + moduleInfoFile.getCanonicalPath();
                    URL urlResource = new URL(jarPath);
                    Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    boolean accessible = method.isAccessible();
                    if (!accessible) {
                        method.setAccessible(true);
                    }
                    URLClassLoader classLoader = (URLClassLoader) this.getClass().getClassLoader();
                    method.invoke(classLoader, urlResource);
                    method.setAccessible(accessible);

                    moduleJarPathList.add(moduleInfoFile.getCanonicalPath());
                }


                if (moduleInfoFile.getName().equals(MODULE_CONFIG_FILE_NAME)) {
                    moduleConfigPath = moduleInfoFile.getCanonicalPath();
                }
            }

            if (moduleJarPathList.isEmpty() || StringUtils.isEmpty(moduleConfigPath)) {
                continue;
            }

            ModuleInfo moduleInfo = new ModuleInfo(moduleJarPathList, moduleConfigPath);

            moduleInfoList.add(moduleInfo);
        }
        if (moduleInfoList.isEmpty()) {
            throw new Exception("There is no available module info error,modules dir(" + modulesDir + ").");
        }

        return;
    }

    private Map<String, Map<String, List<RequestServiceInfo>>> loadModuleConfigInfo(
            ApplicationContext applicationContext) throws Exception {
        Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap = new HashMap<>(COLLECTION_INIT_SIZE);
        Map<String, String> requestServiceInitMethodMap = new HashMap<>(COLLECTION_INIT_SIZE);

        List<ModuleJobInfo> moduleJobInfoList = new ArrayList<>();

        for (int i = 0; i < moduleInfoList.size(); i++) {
            String moduleConfigPath = moduleInfoList.get(i).getConfigPath();

            SAXReader moduleConfigReader = new SAXReader();
            Document moduleConfigDocument = moduleConfigReader.read(new File(moduleConfigPath));
            if (moduleConfigDocument == null) {
                LOGGER.error("Read module config info error,config path(" + moduleConfigPath + ").");
                continue;
            }

            String basePackage = moduleConfigDocument.getRootElement().element(XML_ELEMENT_BASE_PACKAGE).getText();
            if (StringUtils.isEmpty(basePackage)) {
                LOGGER.error("Get service base package info error,base package(" + basePackage + ").");
                continue;
            }

            Element propertiesRootElement = moduleConfigDocument.getRootElement().element(XML_ELEMENT_PROPERTIES);
            loadModulePropertyInfo(applicationContext, propertiesRootElement);

            Element servicesRootElement = moduleConfigDocument.getRootElement().element(XML_ELEMENT_SERVICES);
            loadModuleServiceInfo(applicationContext, servicesRootElement,
                    requestServiceInfoMap, requestServiceInitMethodMap);

            Element jobRootElement = moduleConfigDocument.getRootElement().element(XML_ELEMENT_JOBS);
            loadModuleJobInfo(jobRootElement, moduleJobInfoList);
        }

        if (runTimeProperties.isBeanSwitchWebsocket()) {
            if (!registerHttpApiService(applicationContext, requestServiceInfoMap)) {
                throw new Exception("Register http api service info error.");
            }
        }

        if (runTimeProperties.isBeanSwitchWebsocket()) {
            registerHttpApiJob(moduleJobInfoList);
        }

        for (Map.Entry<String, String> serviceEntry : requestServiceInitMethodMap.entrySet()) {
            Object obj = applicationContext.getBean(serviceEntry.getKey());
            Method method = obj.getClass().getMethod(serviceEntry.getValue());
            method.invoke(obj);
        }

        QuartzJobTools quartzJobTools =
                (QuartzJobTools) applicationContext.getBean(QuartzJobTools.class);
        if (quartzJobTools == null) {
            throw new Exception("Get context bean info error.");
        }

        for (ModuleJobInfo moduleJobInfo : moduleJobInfoList) {
            Class jobClass = applicationContext.getClassLoader().loadClass(moduleJobInfo.getJobClassName());

            if (!quartzJobTools.addJob(jobClass, moduleJobInfo.getJobName(), moduleJobInfo.getJobGroupName(),
                    moduleJobInfo.getTriggerName(), moduleJobInfo.getTriggerGroupName(),
                    moduleJobInfo.getCronExpression(), moduleJobInfo.getJobPropertiesMap())) {
                throw new Exception("Add job error, job name(" + moduleJobInfo.getJobName() + ").");
            }
        }

        return requestServiceInfoMap;
    }

    private Map<String, String> getPropertyInfoMap(Element propertyPropertiesRootElement) {
        if (propertyPropertiesRootElement != null) {
            Map<String, String> propertyPropertiesMap = new HashMap<>(COLLECTION_INIT_SIZE);
            for (Iterator propertyPropertyIter = propertyPropertiesRootElement.elementIterator();
                 propertyPropertyIter.hasNext(); ) {
                Element propertyPropertylEement = (Element) propertyPropertyIter.next();

                String propertyName = propertyPropertylEement.attributeValue(XML_ELEMENT_NAME);
                String propertyValue = propertyPropertylEement.getText();

                if (StringUtils.isEmpty(propertyName) || StringUtils.isEmpty(propertyValue)) {
                    LOGGER.info("Get property info error,name("
                            + propertyName + "),value(" + propertyValue + ").");
                    continue;
                }

                propertyPropertiesMap.put(propertyName, propertyValue);
            }
            return propertyPropertiesMap;
        }
        return null;
    }

    private void loadModulePropertyInfo(ApplicationContext applicationContext, Element propertiesRootElement)
            throws Exception {
        if (propertiesRootElement != null) {
            for (Iterator iter = propertiesRootElement.elementIterator(); iter.hasNext(); ) {
                Element propertyElement = (Element) iter.next();

                String name = propertyElement.element(XML_ELEMENT_NAME).getText();
                if (StringUtils.isEmpty(name)) {
                    throw new Exception("Get property info error,name(" + name + ").");
                }

                Element propertyPropertiesRootElement = propertyElement.element(XML_ELEMENT_PROPERTIES);
                Map<String, String> propertyPropertiesMap = getPropertyInfoMap(propertyPropertiesRootElement);
                if (propertyPropertiesMap != null) {
                    Object obj = applicationContext.getBean(name);
                    setPropertiesInfo(obj, propertyPropertiesMap);
                }
            }
        }
    }

    private void loadModuleServiceInfo(ApplicationContext applicationContext, Element servicesRootElement,
                                       Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap,
                                       Map<String, String> requestServiceInitMethodMap) throws Exception {
        if (servicesRootElement != null) {
            for (Iterator iter = servicesRootElement.elementIterator(); iter.hasNext(); ) {
                Element serviceElement = (Element) iter.next();

                String uri = serviceElement.elementText(XML_ELEMENT_URI);
                String name = serviceElement.elementText(XML_ELEMENT_NAME);
                String type = serviceElement.elementText(XML_ELEMENT_TYPE);
                String enable = serviceElement.elementText(XML_ELEMENT_ENABLE);
                String method = serviceElement.elementText(XML_ELEMENT_METHOD);
                String initMethod = serviceElement.elementText(XML_ELEMENT_INIT_METHOD);
                String authFilterName = serviceElement.elementText(XML_ELEMENT_AUTH_FILTER_NAME);
                String userRoleName = serviceElement.elementText(XML_ELEMENT_USER_ROLE_NAME);
                String paramBeanClassName = serviceElement.elementText(XML_ELEMENT_PARAM_BEAN_CLASS_NAME);

                if (!StringUtils.isEmpty(enable) && enable.equals(Boolean.FALSE.toString())) {
                    continue;
                }

                if (StringUtils.isEmpty(uri) || StringUtils.isEmpty(name)
                        || StringUtils.isEmpty(type) || StringUtils.isEmpty(method)) {
                    throw new Exception("Get service info error,uri("
                            + uri + "),name(" + name + "),type(" + type + "),method(" + method + ").");
                }
                method = method.toLowerCase();

                RequestServiceInfo requestServiceInfo = new RequestServiceInfo();
                requestServiceInfo.setServiceName(name);
                requestServiceInfo.setMethod(method);
                requestServiceInfo.setType(type);

                if (!StringUtils.isEmpty(authFilterName)) {
                    IAuthorizationFilter authorizationFilter
                            = (IAuthorizationFilter) applicationContext.getBean(authFilterName);
                    requestServiceInfo.setAuthorizationFilter(authorizationFilter);
                }

                if (!StringUtils.isEmpty(userRoleName)) {
                    String[] temp = userRoleName.split(",");
                    List<String> userRoleNameList = Arrays.asList(temp);
                    requestServiceInfo.setUserRoleNameList(userRoleNameList);
                }

                if (!StringUtils.isEmpty(paramBeanClassName)) {
                    Class paramBeanClass = applicationContext.getClassLoader().loadClass(paramBeanClassName);
                    requestServiceInfo.setParamClass(paramBeanClass);
                }

                if (!StringUtils.isEmpty(initMethod)) {
                    requestServiceInitMethodMap.put(name, initMethod);
                }

                Map<String, List<RequestServiceInfo>> subRequestServiceInfoMap = requestServiceInfoMap.get(uri);
                if (subRequestServiceInfoMap == null) {
                    subRequestServiceInfoMap = new HashMap<>(COLLECTION_INIT_SIZE);
                }

                List<RequestServiceInfo> requestServiceInfoList = subRequestServiceInfoMap.get(method);
                if (requestServiceInfoList == null) {
                    requestServiceInfoList = new ArrayList<>();
                }
                requestServiceInfoList.add(requestServiceInfo);
                subRequestServiceInfoMap.put(method, requestServiceInfoList);
                requestServiceInfoMap.put(uri, subRequestServiceInfoMap);

                Element servicePropertiesRootElement = serviceElement.element(XML_ELEMENT_PROPERTIES);
                Map<String, String> propertyPropertiesMap = getPropertyInfoMap(servicePropertiesRootElement);
                if (propertyPropertiesMap != null) {
                    Object obj = applicationContext.getBean(name);
                    setPropertiesInfo(obj, propertyPropertiesMap);
                }
            }
        }
    }

    private void loadModuleJobInfo(Element jobRootElement,
                                   List<ModuleJobInfo> moduleJobInfoList) throws Exception {
        if (jobRootElement != null) {
            for (Iterator jobIter = jobRootElement.elementIterator(); jobIter.hasNext(); ) {
                Element jobElement = (Element) jobIter.next();

                String jobClassName = jobElement.element(XML_ELEMENT_CLASS_NAME).getText();
                String jobName = jobElement.element(XML_ELEMENT_NAME).getText();
                String cronExpression = jobElement.element(XML_ELEMENT_CRON_EXPRESSION).getText();
                String enable = jobElement.elementText(XML_ELEMENT_ENABLE);

                if (!StringUtils.isEmpty(enable) && enable.equals(Boolean.FALSE.toString())) {
                    continue;
                }

                if (StringUtils.isEmpty(jobClassName) || StringUtils.isEmpty(jobName)
                        || StringUtils.isEmpty(cronExpression)) {
                    throw new Exception("Get job info error,job class name(" + jobClassName + "),"
                            + "job name(" + jobName + ")," + "cron expression(" + cronExpression + ").");
                }

                Element jobPropertiesRootElement = jobElement.element(XML_ELEMENT_PROPERTIES);
                Map<String, String> jobPropertiesMap = new HashMap<>(COLLECTION_INIT_SIZE);
                if (jobPropertiesRootElement != null) {

                    for (Iterator jobPropertyIter = jobPropertiesRootElement.elementIterator();
                         jobPropertyIter.hasNext(); ) {
                        Element jobPropertylEement = (Element) jobPropertyIter.next();

                        String propertyName = jobPropertylEement.attributeValue(XML_ELEMENT_NAME);
                        String propertyValue = jobPropertylEement.getText();

                        if (StringUtils.isEmpty(propertyName) || StringUtils.isEmpty(propertyValue)) {
                            LOGGER.info("Get job property info error,name("
                                    + propertyName + "),value(" + propertyValue + ").");
                            continue;
                        }

                        jobPropertiesMap.put(propertyName, propertyValue);
                    }
                }

                moduleJobInfoList.add(new ModuleJobInfo(jobClassName, jobName, cronExpression, jobPropertiesMap));
            }
        }
    }


    private Map<String, MqConsumerInfo> initializeMq(
            Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap) throws Exception {
        Map<String, MqConsumerInfo> mqConsumerInfoMap = new HashMap<>(COLLECTION_INIT_SIZE);
        for (Map.Entry<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMapEntry
                : requestServiceInfoMap.entrySet()) {
            String uri = requestServiceInfoMapEntry.getKey();
            Map<String, List<RequestServiceInfo>> subRequestServiceInfoMap = requestServiceInfoMapEntry.getValue();
            for (Map.Entry<String, List<RequestServiceInfo>> subRequestServiceInfoMapEntry
                    : subRequestServiceInfoMap.entrySet()) {
                String method = subRequestServiceInfoMapEntry.getKey();
                List<RequestServiceInfo> requestServiceInfoList = subRequestServiceInfoMapEntry.getValue();
                for (RequestServiceInfo requestServiceInfo : requestServiceInfoList) {
                    String type = requestServiceInfo.getType();
                    if (!RequestServiceInfo.SERVICE_TYPE_MSG.equalsIgnoreCase(type)) {
                        continue;
                    }
                    String[] topicTags = uri.split("/");
                    if (topicTags.length != MSG_URI_PART_NUM) {
                        continue;
                    }

                    String topic = topicTags[0];
                    if (mqConsumerInfoMap.get(topic) == null) {
                        MqConsumerInfo mqConsumerInfo = new MqConsumerInfo();
                        mqConsumerInfo.setTopic(topic);
                        mqConsumerInfo.setMethod(method);

                        ContainerProperties properties = new ContainerProperties(topic);

                        if (!springKafkaConsumerConfig.getEnableAutoCommit()) {
                            properties.setMessageListener(new KafkaAckConsumerListener());
                        } else {
                            if (springKafkaConsumerConfig.getMaxPollRecords() > 1) {
                                properties.setMessageListener(new KafkaBatchConsumerListener());
                            } else {
                                properties.setMessageListener(new KafkaConsumerListener());
                            }
                        }

                        if (RequestServiceInfo.SERVICE_METHOD_MSG_BROADCAST.equalsIgnoreCase(method)) {
                            properties.setGroupId(springKafkaConsumerConfig.getGroupId() + UUID.randomUUID().toString());
                        }

                        KafkaMessageListenerContainer kafkaMessageListenerContainer
                                = new KafkaMessageListenerContainer(consumerFactory, properties);
                        mqConsumerInfo.setKafkaMessageListenerContainer(kafkaMessageListenerContainer);
                        mqConsumerInfoMap.put(topic, mqConsumerInfo);

                    }
                }
            }
        }
        return mqConsumerInfoMap;
    }

    private void loadServiceMonitorAopInfo(ApplicationContext applicationContext) {

        List<IHttpServiceMonitorAop> httpServiceMonitorAopList = new ArrayList<>();
        Map<String, IHttpServiceMonitorAop> httpServiceMonitorAopMap =
                applicationContext.getBeansOfType(IHttpServiceMonitorAop.class);
        for (Map.Entry<String, IHttpServiceMonitorAop> entry : httpServiceMonitorAopMap.entrySet()) {
            IHttpServiceMonitorAop httpServiceMonitorAop = entry.getValue();
            String interfaceName = IHttpServiceMonitorAop.class.getName();
            String className = entry.getKey();
            if (!interfaceName.contains(className)) {
                httpServiceMonitorAopList.add(httpServiceMonitorAop);
            }
        }

        HttpServiceMonitorAop httpServiceMonitorAop =
                (HttpServiceMonitorAop) applicationContext.getBean(HttpServiceMonitorAop.class);
        if (httpServiceMonitorAop != null) {
            httpServiceMonitorAop.setHttpServiceMonitorAopList(httpServiceMonitorAopList);
        }

        if (runTimeProperties.isBeanSwitchWebsocket()) {
            List<IWsServiceMonitorAop> wsServiceMonitorAopList = new ArrayList<>();
            Map<String, IWsServiceMonitorAop> wsServiceMonitorAopMap =
                    applicationContext.getBeansOfType(IWsServiceMonitorAop.class);
            for (Map.Entry<String, IWsServiceMonitorAop> entry : wsServiceMonitorAopMap.entrySet()) {
                IWsServiceMonitorAop wsServiceMonitorAop = entry.getValue();
                String interfaceName = IWsServiceMonitorAop.class.getName();
                String className = entry.getKey();
                if (!interfaceName.contains(className)) {
                    wsServiceMonitorAopList.add(wsServiceMonitorAop);
                }
            }

            WsServiceMonitorAop wsServiceMonitorAop =
                    (WsServiceMonitorAop) applicationContext.getBean(WsServiceMonitorAop.class);
            if (wsServiceMonitorAop != null) {
                wsServiceMonitorAop.setWsServiceMonitorAopList(wsServiceMonitorAopList);
            }
        }

        if (runTimeProperties.isBeanSwitchMqConsumer()) {
            List<IMsgServiceMonitorAop> msgServiceMonitorAopList = new ArrayList<>();
            Map<String, IMsgServiceMonitorAop> msgServiceMonitorAopMap =
                    applicationContext.getBeansOfType(IMsgServiceMonitorAop.class);
            for (Map.Entry<String, IMsgServiceMonitorAop> entry : msgServiceMonitorAopMap.entrySet()) {
                IMsgServiceMonitorAop msgServiceMonitorAop = entry.getValue();
                String interfaceName = IMsgServiceMonitorAop.class.getName();
                String className = entry.getKey();
                if (!interfaceName.contains(className)) {
                    msgServiceMonitorAopList.add(msgServiceMonitorAop);
                }
            }

            MsgServiceMonitorAop msgServiceMonitorAop =
                    (MsgServiceMonitorAop) applicationContext.getBean(MsgServiceMonitorAop.class);
            if (msgServiceMonitorAop != null) {
                msgServiceMonitorAop.setMsgServiceMonitorAopList(msgServiceMonitorAopList);
            }
        }
    }

    private boolean registerHttpApiService(ApplicationContext applicationContext,
                                           Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap) {
        IAuthorizationFilter authorizationFilter = applicationContext.getBean(HttpJwtAuthFilterImpl.class);
        if (authorizationFilter == null) {
            LOGGER.error("Load jwt authentication filter bean error.");
            return false;
        }

        List<String> userRoleNameList = null;
        if (!StringUtils.isEmpty(runTimeProperties.getApiAccessRoleSendMsg())) {
            String[] userRoleNames = runTimeProperties.getApiAccessRoleSendMsg().split(",");
            userRoleNameList = Arrays.asList(userRoleNames);
        }

        RequestServiceInfo sendMsgServiceInfo = new RequestServiceInfo();
        sendMsgServiceInfo.setServiceName(HTTP_API_SERVICE_SEND_MSG);
        sendMsgServiceInfo.setMethod(HTTP_METHOD_POST);
        sendMsgServiceInfo.setType(SERVICE_TYPE_HTTP);
        sendMsgServiceInfo.setParamClass(HttpSendMsgRequestMsgInfo.class);
        sendMsgServiceInfo.setUserRoleNameList(userRoleNameList);
        sendMsgServiceInfo.setAuthorizationFilter(authorizationFilter);

        Map<String, List<RequestServiceInfo>> subRequestServiceInfoMap = new HashMap<>(COLLECTION_INIT_SIZE);
        List<RequestServiceInfo> requestServiceInfoList = new ArrayList<>();
        requestServiceInfoList.add(sendMsgServiceInfo);
        subRequestServiceInfoMap.put(HTTP_METHOD_POST, requestServiceInfoList);
        requestServiceInfoMap.put(HTTP_API_SERVICE_SEND_MSG_URI, subRequestServiceInfoMap);

        return true;
    }

    private void registerHttpApiJob(List<ModuleJobInfo> moduleJobInfoList) {
        Map<String, String> jobPropertiesMap = new HashMap<>(COLLECTION_INIT_SIZE);
        ModuleJobInfo sendAgentMsgJobInfo
                = new ModuleJobInfo(SendAgentMsgJob.class.getTypeName(),
                SendAgentMsgJob.class.getName(), HTTP_API_JOB_CRON_EXPRESSION, jobPropertiesMap);
        moduleJobInfoList.add(sendAgentMsgJobInfo);

        ModuleJobInfo removeInvaildWsSessionInfoJobInfo
                = new ModuleJobInfo(RemoveInvaildWsSessionInfoJob.class.getTypeName(),
                RemoveInvaildWsSessionInfoJob.class.getName(), HTTP_API_JOB_CRON_EXPRESSION, jobPropertiesMap);
        moduleJobInfoList.add(removeInvaildWsSessionInfoJobInfo);
    }


    private void setPropertiesInfo(Object obj, Map<String, String> propertiesMap)
            throws Exception {
        for (Map.Entry<String, String> propertyEntry : propertiesMap.entrySet()) {
            String fieldName = propertyEntry.getKey();
            String fieldValue = propertyEntry.getValue();
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            Method setMethod = pd.getWriteMethod();

            Class type = pd.getPropertyType();
            if (Integer.class.equals(type) || int.class.equals(type)) {
                setMethod.invoke(obj, Integer.valueOf(fieldValue));
            } else if (Long.class.equals(type) || long.class.equals(type)) {
                setMethod.invoke(obj, Long.valueOf(fieldValue));
            } else if (Short.class.equals(type) || short.class.equals(type)) {
                setMethod.invoke(obj, Short.valueOf(fieldValue));
            } else if (Float.class.equals(type) || float.class.equals(type)) {
                setMethod.invoke(obj, Float.valueOf(fieldValue));
            } else if (Double.class.equals(type) || double.class.equals(type)) {
                setMethod.invoke(obj, Double.valueOf(fieldValue));
            } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                setMethod.invoke(obj, Boolean.valueOf(fieldValue));
            } else if (String.class.equals(type)) {
                setMethod.invoke(obj, fieldValue);
            } else {
                throw new Exception("Unsupport value type,field name(" + fieldName + "),type(" + type.getName() + ").");
            }
        }
    }


}
