package com.projn.alps.alpsgenerator.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.projn.alps.alpsgenerator.api.AbstractGeneratedJavaFile;
import com.projn.alps.alpsgenerator.api.dom.java.*;
import com.projn.alps.alpsgenerator.generator.JavaBeanGenerator;
import com.projn.alps.alpsgenerator.generator.MybatisConfigurationGenerator;
import com.projn.alps.alpsgenerator.struct.MsgBodyInfo;
import com.projn.alps.alpsgenerator.struct.MsgPropertyInfo;
import com.projn.alps.alpsgenerator.struct.RequestParamInfo;
import com.projn.alps.define.CommonDefine;
import com.projn.alps.exception.HttpException;
import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.service.IComponentsHttpService;
import com.projn.alps.service.IComponentsMsgService;
import com.projn.alps.service.IComponentsWsService;
import com.projn.alps.struct.*;
import com.projn.alps.tool.HttpControllerTools;
import com.projn.alps.util.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;
import static com.projn.alps.define.CommonDefine.POINT_DIVISION_FLAG;
import static com.projn.alps.struct.RequestServiceInfo.*;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * generator tools
 *
 * @author : sunyuecheng
 */
@Component("GeneratorTools")
public class GeneratorTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorTools.class);

    private static final String XML_ELEMENT_API_FILE_PATH = "api_file_path";
    private static final String XML_ELEMENT_API_VERSION = "api_version";
    private static final String XML_ELEMENT_PARENT_GROUP_ID = "parent_group_id";
    private static final String XML_ELEMENT_PARENT_ARTIFACT_ID = "parent_artifact_id";
    private static final String XML_ELEMENT_OUTPUT_DIR = "output_dir";
    private static final String XML_ELEMENT_RECOVER_EXIST = "recover_exist";
    private static final String XML_ELEMENT_JDBC_CONFIG = "jdbc_config";
    private static final String XML_ELEMENT_DRIVER_CLASS = "driver_class";
    private static final String XML_ELEMENT_CONNECTION_URL = "connection_url";
    private static final String XML_ELEMENT_USER_NAME = "user_name";
    private static final String XML_ELEMENT_PASSWORD = "password";
    private static final String XML_ELEMENT_MODULE_CONFIG = "module_config";
    private static final String XML_ELEMENT_ARTIFACT_ID = "artifact_id";
    private static final String XML_ELEMENT_BASE_PACKAGE = "base_package";
    private static final String XML_ELEMENT_BEAN_CONFIG = "bean_config";
    private static final String XML_ELEMENT_TAG = "tag";
    private static final String XML_ELEMENT_DAO_CONFIG = "dao_config";
    private static final String XML_ELEMENT_TABLE_CONFIG = "table_config";
    private static final String XML_ELEMENT_SCHEMA_NAME = "schema_name";
    private static final String XML_ELEMENT_TABLE_NAME = "table_name";
    private static final String XML_ELEMENT_DOMAIN_NAME = "domain_name";
    private static final String XML_ELEMENT_MAPPER_NAME = "mapper_name";

    private static final String XML_ELEMENT_MODEL = "model";
    private static final String XML_ELEMENT_PROPERTIES = "properties";
    private static final String XML_ELEMENT_SERVICES = "services";
    private static final String XML_ELEMENT_SERVICE = "service";
    private static final String XML_ELEMENT_NAME = "name";
    private static final String XML_ELEMENT_URI = "uri";
    private static final String XML_ELEMENT_TYPE = "type";
    private static final String XML_ELEMENT_METHOD = "method";
    private static final String XML_ELEMENT_PARAM_BEAN_CLASS_NAME = "param_bean_class_name";

    private static final String YML_ELEMENT_TAGS = "tags";
    private static final String YML_ELEMENT_NAME = XML_ELEMENT_NAME;
    private static final String YML_ELEMENT_PATHS = "paths";
    private static final String YML_ELEMENT_SUMMARY = "summary";
    private static final String YML_ELEMENT_CONSUMES = "consumes";
    private static final String YML_ELEMENT_OPERATION_ID = "operationId";
    private static final String YML_ELEMENT_PARAMETERS = "parameters";
    private static final String YML_ELEMENT_REQUEST_BODY = "requestBody";
    private static final String YML_ELEMENT_RESPONSES = "responses";
    private static final String YML_ELEMENT_HTTP_STATUS_OK = "200";
    private static final String YML_ELEMENT_REF = "$ref";
    private static final String YML_ELEMENT_CONTENT = "content";
    private static final String YML_ELEMENT_APPLICATION_JSON = "application/json";
    private static final String YML_ELEMENT_APPLICATION_FILE = "application/file";
    private static final String YML_ELEMENT_SCHEMA = "schema";
    private static final String YML_ELEMENT_TYPE = "type";
    private static final String YML_ELEMENT_PROPERTIES = "properties";

    private static final String MODULE_PARAM_LOCATION_BODY = "body";
    private static final String MODULE_PARAM_LOCATION_PATH = "path";

    private static final String MODULE_SRC_PATH = "*src*main*java";
    private static final String MODULE_RESOURCE_XML_PATH = "*src*main*resources*xml";

    private static final String MODULE_SERVICE_BEAN_TAIL = "ServiceImpl";
    private static final String MODULE_SERVICE_IMPL_BEAN_TAIL = "ServiceImpl";
    private static final String MODULE_RESQUEST_BEAN_TAIL = "RequestInfo";
    private static final String MODULE_RESPONSE_BEAN_TAIL = "ResponseInfo";

    private static final String MODULE_MSG_REQUEST_PACKAGE_TAIL = ".msg.request.";
    private static final String MODULE_MSG_REQUEST_TYPE_PACKAGE_TAIL = ".msg.request.type.";
    private static final String MODULE_MSG_RESPONSE_PACKAGE_TAIL = ".msg.response.";
    private static final String MODULE_MSG_RESPONSE_TYPE_PACKAGE_TAIL = ".msg.response.type.";
    private static final String MODULE_SERVICE_PACKAGE_TAIL = ".service.impl.";
    private static final String MODULE_CONTROLLER_PACKAGE_TAIL = ".controller.";
    private static final String MODULE_CONTROLLER_NAME_TAIL = "ModuleController";
    private static final String MODULE_CONTROLLER_MATHOD_DOC_FROMAT = "/**%n"
            + "     * service bean :%n"
            + "     * @see %s%n"
            + "     * request bean :%n"
            + "     * @see %s%n"
            + "     * response bean :%n"
            + "     * @see %s%n"
            + "     */";

    private static final String JAVA_CLASS_COMPONENT_ANNOUNCE_FORMAT = "@Component(\"%s\")";
    private static final String JAVA_CLASS_CONTROLLER_ANNOUNCE = "@Controller";
    private static final String JAVA_CLASS_CROSS_ORIGIN_ANNOUNCE = "@CrossOrigin(origins = \"*\", maxAge = 3600)";
    private static final String JAVA_IMPORT_TYPE_CROSS_ORIGIN = "org.springframework.web.bind.annotation.*";
    private static final String JAVA_METHOD_OVERRIDE_ANNOUNCE = "@Override";
    private static final String JAVA_FIELD_PARAM_LOCATION_ANNOUNCE_FROMAT
            = "@ParamLocation(location = ParamLocationType.%s)";
    private static final String JAVA_IMPORT_TYPE_PARAM_LOCATION_TYPE
            = "com.projn.alps.msg.filter.ParamLocationType";
    private static final String JAVA_FIELD_AUTOWIRED_FORMAT = "@Autowired";
    private static final String JAVA_METHOD_RETURN_CONTROLLER = "@ResponseBody DeferredResult<Object>";
    private static final String JAVA_METHOD_REQUEST_MAPPING_ANNOUNCE
            = "@RequestMapping(value = {\"%s\"}, method = {%s})";
    private static final String JAVA_METHOD_REQUEST_BODY_ANNOUNCE = "@RequestBody(required = false)";
    private static final String JAVA_METHOD_REQUEST_FILE_ANNOUNCE = "@RequestParam(\"file\")";
    private static final String JAVA_METHOD_PATH_VARIABLE_ANNOUNCE_FORMAT = "@PathVariable(name = \"%s\")";
    private static final String JAVA_IMPORT_TYPE_STAR_TAIL = ".*";

    private static final String SHARP_FLAG = "#";

    private static final String JAVA_MODEL_TAIL = ".domain";
    private static final String SQL_MAP_TAIL = ".mapper";
    private static final String JAVA_CLIENT_TAIL = ".dao";

    private static final String TEMPLATE_INSTALL_DIR_NAME = "template/install";
    private static final String INSTALL_DIR_NAME = "install";
    private static final String TEMPLATE_TEST_DIR_MODULE_NAME = "template/test/module";
    private static final String TEST_DIR_MODULE_NAME = "test/module";
    private static final String TEMPLATE_TEST_DIR_SERVICE_NAME
            = "template/install/alpsmicroservice-install/alpsmicroservice/context";
    private static final String TEST_DIR_SERVICE_NAME = "test/alpsmicroservice/context";
    private static final String TEMPLATE_MAVEN_PLUGIN_CONFIG_DIR_NAME = "template/maven-plugin-config";
    private static final String MAVEN_PLUGIN_CONFIG_DIR_NAME = "maven-plugin-config";
    private static final String TEMPLATE_POM_NAME = "template/pom_template.xml";
    private static final String TEMPLATE_MODULE_POM_NAME = "template/module_pom_template.xml";
    private static final String POM_FILE_NAME = "pom.xml";
    private static final String MODULE_CONFIG_FILE_NAME = "module_config.xml";
    private static final String MODULE_DIR_NAME = "modules";

    private static final int OPEN_API_VERSION_2 = 2;
    private static final int OPEN_API_VERSION_3 = 3;

    private static final int TAG_TYPE_PART_NUM = 2;
    private static final int ENUM_TYPE_PART_NUM = 2;

    private String apiFilePath;
    private int apiVersion;
    private String parentGroupId;
    private String parentArtifactId;
    private String groupId;
    private String outputDir;
    private boolean recoverExist = true;

    private String driverClass;
    private String connectionUrl;
    private String userName;
    private String password;

    private Map<String, Object> dataMap;
    private String pomTemplateContent;

    /**
     * load config
     *
     * @param configPath :
     * @return boolean :
     */
    public boolean loadConfig(String configPath) {

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configPath);
            if (document == null) {
                LOGGER.error("Read module config info error.");
                return false;
            }

            Element rootElement = document.getRootElement();
            if (!parseModuleCommonInfo(rootElement)) {
                return false;
            }

            if (!generatorProjectInfo()) {
                return false;
            }

            for (Iterator iter = rootElement.elementIterator(XML_ELEMENT_MODULE_CONFIG); iter.hasNext(); ) {
                Element moduleConfigElement = (Element) iter.next();
                if (moduleConfigElement == null) {
                    LOGGER.error("Get module config info error.");
                    return false;
                }

                String artifactId = moduleConfigElement.elementText(XML_ELEMENT_ARTIFACT_ID);
                String basePackage = moduleConfigElement.elementText(XML_ELEMENT_BASE_PACKAGE);
                if (StringUtils.isEmpty(artifactId) || StringUtils.isEmpty(basePackage)) {
                    LOGGER.error("Get module config info error,artifact id("
                            + artifactId + "),base package(" + basePackage + ").");
                    return false;
                }

                String modulePath = outputDir + File.separator + MODULE_DIR_NAME + File.separator + artifactId;
                File modulePathFile = new File(modulePath);
                if (!modulePathFile.exists()) {
                    if (!modulePathFile.mkdirs()) {
                        LOGGER.error("Make module dir error,module name({}).", artifactId);
                        return false;
                    }
                }

                String moduleSrcPath = modulePath + MODULE_SRC_PATH.replace("*", File.separator);
                File moduleSrcPathFile = new File(moduleSrcPath);
                if (!moduleSrcPathFile.exists()) {
                    if (!moduleSrcPathFile.mkdirs()) {
                        LOGGER.error("Make module src dir error,module name({}).", artifactId);
                        return false;
                    }
                }

                String moduleResourcePath = modulePath + MODULE_RESOURCE_XML_PATH.replace("*", File.separator);
                File moduleResourcePathFile = new File(moduleResourcePath);
                if (!moduleResourcePathFile.exists()) {
                    if (!moduleResourcePathFile.mkdirs()) {
                        LOGGER.error("Make module resource dir error,module name({}).", artifactId);
                        return false;
                    }
                }

                if (!parseModuleDaoInfo(moduleConfigElement, moduleSrcPath, basePackage, artifactId)) {
                    return false;
                }
                LOGGER.info("Generate module dao info successfully,module name({}).", artifactId);

                if (!parseModuleBeanInfo(moduleConfigElement, moduleSrcPath, moduleResourcePath,
                        basePackage, artifactId)) {
                    return false;
                }
                LOGGER.info("Generate module bean info successfully,module name({}).", artifactId);

                if (!generatorModulePom(modulePath, artifactId)) {
                    return false;
                }
                LOGGER.info("Generate module pom info successfully,module name({}).", artifactId);
            }

        } catch (Exception e) {
            LOGGER.error("Analyse module config error, error info({}).", formatExceptionInfo(e));
        }
        return true;
    }

    private boolean parseModuleCommonInfo(Element rootElement) {
        apiFilePath = rootElement.elementText(XML_ELEMENT_API_FILE_PATH);

        String apiVersionStr = rootElement.elementText(XML_ELEMENT_API_VERSION);
        if (StringUtils.isEmpty(apiVersionStr) || apiVersionStr.length() < 1) {
            LOGGER.error("Analyse api version error.");
            return false;
        }
        apiVersion = Integer.parseInt(apiVersionStr.substring(0, 1));
        if (apiVersion != OPEN_API_VERSION_2 && apiVersion != OPEN_API_VERSION_3) {
            LOGGER.error("Analyse api version error.");
            return false;
        }

        parentGroupId = rootElement.elementText(XML_ELEMENT_PARENT_GROUP_ID);
        parentArtifactId = rootElement.elementText(XML_ELEMENT_PARENT_ARTIFACT_ID);
        outputDir = rootElement.elementText(XML_ELEMENT_OUTPUT_DIR);
        recoverExist = rootElement.elementText(XML_ELEMENT_RECOVER_EXIST) == null
                ? true : Boolean.parseBoolean(rootElement.elementText(XML_ELEMENT_RECOVER_EXIST));
        if (StringUtils.isEmpty(apiFilePath)
                || StringUtils.isEmpty(parentGroupId) || StringUtils.isEmpty(parentArtifactId)
                || StringUtils.isEmpty(outputDir)) {
            LOGGER.error("Analyse config info error,api file path("
                    + apiFilePath + "),parent group id(" + parentGroupId
                    + "),parent artifact id(" + parentArtifactId
                    + "),output dir(" + outputDir + ").");
            return false;
        }
        outputDir = outputDir + File.separator + parentArtifactId;
        groupId = parentGroupId + POINT_DIVISION_FLAG + parentArtifactId;
        String modulePomTemplatePath = System.getProperty("user.dir") + File.separator + TEMPLATE_MODULE_POM_NAME;

        Yaml yaml = new Yaml();
        File apiFile = new File(apiFilePath);
        if (!apiFile.exists()) {
            LOGGER.error("Api file does not exist, file path({}).", apiFilePath);
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(apiFile)) {
            dataMap = (Map<String, Object>) yaml.load(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            LOGGER.error("Analyse api file info error, error info({}).", formatExceptionInfo(e));
        }

        if (dataMap == null) {
            LOGGER.error("Analyse api file error.");
            return false;
        }

        try {
            pomTemplateContent = FileUtils.readFileByStr(modulePomTemplatePath);
        } catch (Exception e) {
            LOGGER.error("Read pom template file info error, error info({}).", formatExceptionInfo(e));
        }

        Element jdbcConfigElement = rootElement.element(XML_ELEMENT_JDBC_CONFIG);
        if (jdbcConfigElement == null) {
            LOGGER.error("Get jdbc config info error.");
            return false;
        }

        driverClass = jdbcConfigElement.elementText(XML_ELEMENT_DRIVER_CLASS);
        connectionUrl = jdbcConfigElement.elementText(XML_ELEMENT_CONNECTION_URL);
        userName = jdbcConfigElement.elementText(XML_ELEMENT_USER_NAME);
        password = jdbcConfigElement.elementText(XML_ELEMENT_PASSWORD);

        if (StringUtils.isEmpty(driverClass) || StringUtils.isEmpty(connectionUrl)
                || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            LOGGER.error("Analyse jdbc config info error, driver class({}), "
                            + "connection url({}), user name({}), password({}).",
                    driverClass, connectionUrl, userName, password);
            return false;
        }

        return true;
    }

    private boolean generatorProjectInfo() {
        File projectPathFile = new File(outputDir);
        if (!projectPathFile.exists()) {
            if (!projectPathFile.mkdirs()) {
                LOGGER.error("Make project dir error,project name({}).", parentArtifactId);
                return false;
            }
        }
        try {
            String templatePomPath = System.getProperty("user.dir") + File.separator + TEMPLATE_POM_NAME;
            String targetPomPath = outputDir + File.separator + POM_FILE_NAME;
            String pomContent = FileUtils.readFileByStr(templatePomPath);
            pomContent = pomContent.replace("MODULE_PARENT_GROUP_ID", parentGroupId);
            pomContent = pomContent.replace("MODULE_PARENT_ARTIFACT_ID", parentArtifactId);
            if (!new File(targetPomPath).exists() || recoverExist) {
                FileUtils.writeFileByStr(targetPomPath, pomContent, false);
            }

            String templateInstallPath = System.getProperty("user.dir") + File.separator + TEMPLATE_INSTALL_DIR_NAME;
            String targetInstallPath = outputDir + File.separator + INSTALL_DIR_NAME;
            FileUtils.copyDirectory(templateInstallPath, targetInstallPath);

            String templateTestModulePath = System.getProperty("user.dir")
                    + File.separator + TEMPLATE_TEST_DIR_MODULE_NAME;
            String targetTestModulePath = outputDir + File.separator + TEST_DIR_MODULE_NAME;
            FileUtils.copyDirectory(templateTestModulePath, targetTestModulePath);

            String templateTestServicePath = System.getProperty("user.dir")
                    + File.separator + TEMPLATE_TEST_DIR_SERVICE_NAME;
            String targetTestServicePath = outputDir + File.separator + TEST_DIR_SERVICE_NAME;
            FileUtils.copyDirectory(templateTestServicePath, targetTestServicePath);

            String templateMavenPluginConfigPath = System.getProperty("user.dir") + File.separator
                    + TEMPLATE_MAVEN_PLUGIN_CONFIG_DIR_NAME;
            String targetMavenPluginConfigPath = outputDir + File.separator + MAVEN_PLUGIN_CONFIG_DIR_NAME;
            FileUtils.copyDirectory(templateMavenPluginConfigPath, targetMavenPluginConfigPath);
        } catch (Exception e) {
            LOGGER.error("Generate project info error, error info({}).", formatExceptionInfo(e));
            return false;
        }
        return true;
    }

    private boolean parseModuleDaoInfo(Element moduleConfigElement, String moduleSrcPath,
                                       String basePackage, String artifactId) {

        MybatisConfigurationGenerator mybatisConfigurationGenerator = new MybatisConfigurationGenerator();
        if (!mybatisConfigurationGenerator.setJdbcConfiguration(driverClass,
                connectionUrl, userName, password)) {
            LOGGER.error("Set mybatis jdbc config info error, driver class({}), "
                            + "connection url({}), user name({}), password({}).",
                    driverClass, connectionUrl, userName, password);
            return false;
        }

        if (!mybatisConfigurationGenerator.setJavaModelConfiguration(
                basePackage + JAVA_MODEL_TAIL, moduleSrcPath)
                || !mybatisConfigurationGenerator.setSqlMapConfiguration(
                basePackage + SQL_MAP_TAIL, moduleSrcPath)
                || !mybatisConfigurationGenerator.setJavaClientConfiguration(
                basePackage + JAVA_CLIENT_TAIL, moduleSrcPath)) {
            LOGGER.error("Set module mybatis bean config info error, module name({}).", artifactId);
            return false;
        }

        Element moduleDaoConfigElement = (Element) moduleConfigElement.element(XML_ELEMENT_DAO_CONFIG);
        if (moduleDaoConfigElement == null) {
            LOGGER.info("There is no module dao config info, module name({}).", artifactId);
            return true;
        }

        int tableCount = 0;
        for (Iterator subIter = moduleDaoConfigElement.elementIterator(XML_ELEMENT_TABLE_CONFIG); subIter.hasNext(); ) {
            Element moduleTableConfigElement = (Element) subIter.next();

            String schemaName = moduleTableConfigElement.elementText(XML_ELEMENT_SCHEMA_NAME);
            String tableName = moduleTableConfigElement.elementText(XML_ELEMENT_TABLE_NAME);
            String domainName = moduleTableConfigElement.elementText(XML_ELEMENT_DOMAIN_NAME);
            String mapperName = moduleTableConfigElement.elementText(XML_ELEMENT_MAPPER_NAME);

            if (!mybatisConfigurationGenerator.addTableConfiguration(schemaName,
                    tableName, domainName, mapperName)) {
                LOGGER.error("Add module mybatis table config info error, schema names({}), "
                                + "table name({}), domain name({}), mapper name({}), module name({}).",
                        schemaName, tableName, domainName, mapperName, artifactId);
                return false;
            }
            tableCount++;
        }

        if (tableCount != 0) {
            if (!generatorModuleDao(mybatisConfigurationGenerator.getConfiguration())) {
                return false;
            }
        }

        return true;
    }

    private boolean generatorModuleDao(Configuration config) {
        try {
            List<String> warnings = new ArrayList<String>();
            Set<String> fullyqualifiedTables = new HashSet<String>();
            Set<String> contexts = new HashSet<String>();

            DefaultShellCallback shellCallback = new DefaultShellCallback(recoverExist);

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);

            ProgressCallback progressCallback = null;

            myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);

        } catch (Exception e) {
            LOGGER.error("Generate module dao info error, error info({}).", formatExceptionInfo(e));
            return false;
        }
        return true;
    }

    private boolean parseModuleBeanInfo(Element moduleConfigElement, String moduleSrcPath, String moduleResourcePath,
                                        String basePackage, String artifactId) {
        Element moduleMsgConfigElement = (Element) moduleConfigElement.element(XML_ELEMENT_BEAN_CONFIG);
        if (moduleMsgConfigElement == null) {
            LOGGER.error("Get module msg config info error,module name({}).", artifactId);
            return false;
        }

        String moduleTag = moduleMsgConfigElement.elementText(XML_ELEMENT_TAG);
        if (StringUtils.isEmpty(moduleTag)) {
            LOGGER.error("Get module msg info error,tag({}), module name({}).", moduleTag, artifactId);
        }

        return parseModuleMsgApiInfo(moduleSrcPath, moduleResourcePath, basePackage, artifactId, moduleTag);
    }

    private boolean parseModuleMsgApiInfo(String moduleSrcPath, String moduleResourcePath,
                                          String basePackage, String artifactId, String moduleTag) {

        if (!checkModuleMsgApiTagExist(artifactId, moduleTag)) {
            return false;
        }

        List<TopLevelClass> allClassList = new ArrayList<>();
        Document configDoc = DocumentHelper.createDocument();
        Element rootElement = configDoc.addElement(XML_ELEMENT_MODEL);
        Element basePackageElement = rootElement.addElement(XML_ELEMENT_BASE_PACKAGE);
        basePackageElement.setText(basePackage);
        Element servicesElement = rootElement.addElement(XML_ELEMENT_SERVICES);

        Map<String, Object> pathMap = (Map<String, Object>) dataMap.get(YML_ELEMENT_PATHS);
        if (pathMap == null || pathMap.isEmpty()) {
            LOGGER.error("Get module api paths info error, module name({}).", artifactId);
            return false;
        }

        String moduleControllerName
                = JavaBeanGenerator.getCamelCaseString(moduleTag, true) + MODULE_CONTROLLER_NAME_TAIL;

        TopLevelClass controllerClass
                = JavaBeanGenerator.getClazz(basePackage + MODULE_CONTROLLER_PACKAGE_TAIL
                + moduleControllerName, null);
        controllerClass.addAnnotation(JAVA_CLASS_CONTROLLER_ANNOUNCE);
        controllerClass.addImportedType(Autowired.class.getTypeName());
        controllerClass.addImportedType(Controller.class.getTypeName());

        FullyQualifiedJavaType fieldType = new FullyQualifiedJavaType(HttpControllerTools.class.getSimpleName());
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fieldType);
        field.setName(JavaBeanGenerator.getCamelCaseString(HttpControllerTools.class.getSimpleName(), false));
        field.addAnnotation(JAVA_FIELD_AUTOWIRED_FORMAT);
        controllerClass.addField(field);
        controllerClass.addImportedType(HttpControllerTools.class.getTypeName());

        for (Map.Entry<String, Object> pathEntry : pathMap.entrySet()) {
            String urlPath = pathEntry.getKey();
            Map<String, Object> urlMethodMap = (Map<String, Object>) pathEntry.getValue();
            if (urlMethodMap == null || urlMethodMap.isEmpty()) {
                LOGGER.error("Get module api url method info error, module name({}).", artifactId);
                return false;
            }

            boolean buildControllerClass = false;
            for (Map.Entry<String, Object> urlMethodEntry : urlMethodMap.entrySet()) {
                String urlMethod = urlMethodEntry.getKey();
                Map<String, Object> urlDetailMap = (Map<String, Object>) urlMethodEntry.getValue();
                if (urlDetailMap == null || urlDetailMap.isEmpty()) {
                    LOGGER.error("Get module api url detail info error, module name({}).", artifactId);
                    return false;
                }

                List<String> tagList = (List<String>) urlDetailMap.get(YML_ELEMENT_TAGS);
                if (tagList == null || tagList.size() != 1 || !tagList.get(0).startsWith(moduleTag)) {
                    continue;
                }

                String[] tagType = tagList.get(0).split(SHARP_FLAG);
                if (tagType.length != TAG_TYPE_PART_NUM) {
                    continue;
                }
                String type = tagType[1];
                if (SERVICE_TYPE_MSG.equalsIgnoreCase(type)) {
                    if (SERVICE_METHOD_HTTP_GET.equalsIgnoreCase(urlMethod)) {
                        urlMethod = SERVICE_METHOD_MSG_NORMAL;
                    } else if (SERVICE_METHOD_HTTP_POST.equalsIgnoreCase(urlMethod)) {
                        urlMethod = SERVICE_METHOD_MSG_ORDER;
                    } else if (SERVICE_METHOD_HTTP_PUT.equalsIgnoreCase(urlMethod)) {
                        urlMethod = SERVICE_METHOD_MSG_BROADCAST;
                    } else {
                        continue;
                    }
                }

                String serviceDesc = (String) urlDetailMap.get(YML_ELEMENT_SUMMARY);
                String operationId = (String) urlDetailMap.get(YML_ELEMENT_OPERATION_ID);
                if (StringUtils.isEmpty(operationId)) {
                    LOGGER.error("Get module api operation id error, path({}), method({}), module name({}).",
                            urlPath, urlMethod, artifactId);
                    return false;
                }
                String serviceName = operationId + MODULE_SERVICE_BEAN_TAIL;
                String serviceImplName = operationId + MODULE_SERVICE_IMPL_BEAN_TAIL;
                String requestInfoBeanName = JavaBeanGenerator.getCamelCaseString(type, true)
                        + operationId + MODULE_RESQUEST_BEAN_TAIL;

                String consumeContentType = YML_ELEMENT_APPLICATION_JSON;
                if (apiVersion == OPEN_API_VERSION_2) {
                    List<String> consumeContentTypeList = (List<String>) urlDetailMap.get(YML_ELEMENT_CONSUMES);
                    if (consumeContentTypeList != null && consumeContentTypeList.size() == 1
                            && !StringUtils.isEmpty(consumeContentTypeList.get(0))) {
                        consumeContentType = consumeContentTypeList.get(0);
                        if (!consumeContentType.equals(YML_ELEMENT_APPLICATION_JSON)
                                && !consumeContentType.equals(YML_ELEMENT_APPLICATION_FILE)) {
                            LOGGER.error("Get module api url consume info error, path({}), "
                                    + "method({}), module name({}).", urlPath, urlMethod, artifactId);
                            return false;
                        }
                    }
                }

                Element serviceElement = servicesElement.addElement(XML_ELEMENT_SERVICE);
                Element uriElement = serviceElement.addElement(XML_ELEMENT_URI);
                uriElement.setText(urlPath);
                Element nameElement = serviceElement.addElement(XML_ELEMENT_NAME);
                nameElement.setText(serviceName);
                Element methodElement = serviceElement.addElement(XML_ELEMENT_METHOD);
                methodElement.setText(urlMethod);
                Element typeElement = serviceElement.addElement(XML_ELEMENT_TYPE);
                typeElement.setText(type);

                String requestInfoClassType = basePackage + MODULE_MSG_REQUEST_PACKAGE_TAIL + requestInfoBeanName;
                TopLevelClass requestInfoClass
                        = JavaBeanGenerator.getClazz(requestInfoClassType, null);
                requestInfoClass.addImportedType(ParamLocation.class.getTypeName());

                Element paramElement = serviceElement.addElement(XML_ELEMENT_PARAM_BEAN_CLASS_NAME);
                paramElement.setText(requestInfoClassType);

                List<Map<String, Object>> paramMapList
                        = (List<Map<String, Object>>) urlDetailMap.get(YML_ELEMENT_PARAMETERS);
                if (paramMapList != null) {
                    List<TopLevelClass> requestInfoClassList = parseModuleMsgApiRequestParamInfo(urlPath,
                            urlMethod, artifactId, basePackage, consumeContentType, requestInfoClass, paramMapList);

                    if (requestInfoClassList != null) {
                        allClassList.addAll(requestInfoClassList);
                    }
                }

                if (apiVersion == OPEN_API_VERSION_3) {
                    Map<String, Object> requestBodyMap
                            = (Map<String, Object>) urlDetailMap.get(YML_ELEMENT_REQUEST_BODY);
                    if (requestBodyMap != null) {
                        List<TopLevelClass> requestInfoClassList = new ArrayList<>();

                        MsgBodyInfo msgBodyInfo = parseModuleMsgApiRequestBodyMapInfo(requestBodyMap);
                        if (msgBodyInfo != null && msgBodyInfo.getMsgBodyMap() != null) {
                            requestInfoClassList = parseModuleMsgApiRequestMsgBodyInfo(urlPath, urlMethod, artifactId,
                                    basePackage, MODULE_MSG_REQUEST_TYPE_PACKAGE_TAIL,
                                    requestInfoClass, msgBodyInfo.getName(), 0, msgBodyInfo.getMsgBodyMap());
                        } else if (msgBodyInfo != null && YML_ELEMENT_APPLICATION_FILE.equals(msgBodyInfo.getName())) {
                            Field filefield = JavaBeanGenerator.getField("file",
                                    null, MultipartFile.class.getSimpleName());
                            filefield.addAnnotation(String.format(JAVA_FIELD_PARAM_LOCATION_ANNOUNCE_FROMAT,
                                    MODULE_PARAM_LOCATION_BODY.toUpperCase()));
                            requestInfoClass.addImportedType(ParamLocation.class.getTypeName());
                            requestInfoClass.addImportedType(JAVA_IMPORT_TYPE_PARAM_LOCATION_TYPE);

                            requestInfoClass.addField(filefield);
                            requestInfoClass.addImportedType(MultipartFile.class.getTypeName());

                            requestInfoClass.addMethod(JavaBeanGenerator.getFieldSetterMethod("file",
                                    MultipartFile.class.getSimpleName()));
                            requestInfoClass.addMethod(JavaBeanGenerator.getFieldGetterMethod("file",
                                    MultipartFile.class.getSimpleName()));

                            consumeContentType = YML_ELEMENT_APPLICATION_FILE;
                        }
                        if (requestInfoClassList != null) {
                            allClassList.addAll(requestInfoClassList);
                        }
                    }
                }
                allClassList.add(requestInfoClass);

                String responseClaseName = null;
                Map<Object, Object> responseBodyMap = (Map<Object, Object>) urlDetailMap.get(YML_ELEMENT_RESPONSES);
                if (responseBodyMap != null) {
                    MsgBodyInfo msgBodyInfo = parseModuleMsgApiResponseBodyMapInfo(responseBodyMap);
                    if (msgBodyInfo != null) {
                        String responseInfoBeanName = operationId + MODULE_RESPONSE_BEAN_TAIL;
                        responseInfoBeanName = JavaBeanGenerator.getCamelCaseString(type, true)
                                + responseInfoBeanName;

                        responseClaseName = basePackage + MODULE_MSG_RESPONSE_PACKAGE_TAIL + responseInfoBeanName;
                        List<TopLevelClass> responseInfoClassList = parseModuleMsgApiResponseMsgBodyInfo(
                                urlPath, urlMethod, artifactId, basePackage,
                                responseInfoBeanName, 0, msgBodyInfo.getMsgBodyMap());

                        if (responseInfoClassList != null) {
                            allClassList.addAll(responseInfoClassList);
                        }
                    }
                }

                TopLevelClass serviceClass = parseModuleMsgApiServiceInfo(type, basePackage,
                        serviceName, serviceImplName, serviceDesc);
                allClassList.add(serviceClass);

                if (SERVICE_TYPE_HTTP.equalsIgnoreCase(type)) {
                    buildControllerClass = true;
                    Set<String> urlPathParamNameSet = new TreeSet<>();
                    if (paramMapList != null) {
                        parseModuleMsgApiRequestPathParamInfo(paramMapList, urlPathParamNameSet);
                    }
                    parseModuleMsgApiControllerMethodInfo(controllerClass,
                            serviceClass.getType().getFullyQualifiedName(),
                            requestInfoClass.getType().getFullyQualifiedName(),
                            responseClaseName,
                            urlPath, urlMethod, operationId,
                            consumeContentType, urlPathParamNameSet);
                }

            }

            if (buildControllerClass) {
                allClassList.add(controllerClass);
            }
        }

        return generatorModuleBeanService(moduleSrcPath, allClassList)
                | generatorModuleConfig(moduleResourcePath, configDoc);
    }

    private boolean checkModuleMsgApiTagExist(String artifactId, String apiFlag) {
        List<Map<String, String>> tagMapList = (List<Map<String, String>>) dataMap.get(YML_ELEMENT_TAGS);
        if (tagMapList == null || tagMapList.isEmpty()) {
            LOGGER.error("Get module api tags info error, module name({}), tag({}).", artifactId, apiFlag);
            return false;
        }

        boolean tagExist = false;
        for (Map<String, String> tagMap : tagMapList) {
            if (tagMap.get(YML_ELEMENT_NAME).startsWith(apiFlag)) {
                tagExist = true;
                break;
            }
        }

        if (!tagExist) {
            LOGGER.error("There is no module api tags info, module name({}), tag({}).", artifactId, apiFlag);
            return false;
        }

        return true;
    }

    private List<TopLevelClass> parseModuleMsgApiRequestParamInfo(String urlPath, String urlMethod,
                                                                  String artifactId, String basePackage,
                                                                  String consumeContentType,
                                                                  TopLevelClass requestInfoClass,
                                                                  List<Map<String, Object>> paramMapList) {
        List<TopLevelClass> topLevelClassList = new ArrayList<>();

        for (Map<String, Object> paramMap : paramMapList) {
            RequestParamInfo requestParamInfo
                    = JSONObject.parseObject(JSON.toJSONString(paramMap), RequestParamInfo.class);
            String type = null;
            String format = null;
            String defaultValue = null;
            String in = null;
            List<String> enums = null;
            String ref = null;

            if (StringUtils.isEmpty(requestParamInfo.getName())
                    || StringUtils.isEmpty(requestParamInfo.getIn())
                    || (requestParamInfo.getSchema() == null && StringUtils.isEmpty(requestParamInfo.getType()))) {
                LOGGER.error("Get module api request param info error, path({}), method({}), module name({}),"
                                + " param name({}), param location({}).",
                        urlPath, urlMethod, artifactId, requestParamInfo.getName(),
                        requestParamInfo.getIn());
                return null;
            }
            in = requestParamInfo.getIn();

            if (requestParamInfo.getSchema() == null) {
                if (StringUtils.isEmpty(requestParamInfo.getType())) {
                    LOGGER.error("Get module api request param info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param type({}).",
                            urlPath, urlMethod, artifactId, requestParamInfo.getName(),
                            requestParamInfo.getIn(), requestParamInfo.getType());
                    return null;
                }
                type = requestParamInfo.getType();
                format = requestParamInfo.getFormat();
                enums = requestParamInfo.getEnums();
                defaultValue = requestParamInfo.getDefaultValue();
            } else {
                RequestParamInfo.SchemaTypeInfo schemaTypeInfo =
                        JSONObject.parseObject(JSON.toJSONString(requestParamInfo.getSchema()),
                                RequestParamInfo.SchemaTypeInfo.class);
                if (StringUtils.isEmpty(schemaTypeInfo.getType())) {
                    schemaTypeInfo.setType("object");
                }
                type = schemaTypeInfo.getType();
                format = schemaTypeInfo.getFormat();
                enums = schemaTypeInfo.getEnums();
                defaultValue = schemaTypeInfo.getDefaultValue();
                ref = schemaTypeInfo.getRef();

                if (!StringUtils.isEmpty(ref)) {
                    MsgBodyInfo msgBodyInfo =
                            parseModuleMsgApiRequestBodyMapInfo(requestParamInfo.getSchema());
                    if (msgBodyInfo == null) {
                        LOGGER.error("Get module api request param object info error, path({}), "
                                        + "method({}), module name({}),"
                                        + " param name({}), param location({}), param ref({}).",
                                urlPath, urlMethod, artifactId, requestParamInfo.getName(),
                                requestParamInfo.getIn(), ref);
                        return null;
                    }

                    List<TopLevelClass> subTopLevelClassList
                            = parseModuleMsgApiRequestMsgBodyInfo(urlPath, urlMethod, artifactId, basePackage,
                            MODULE_MSG_REQUEST_TYPE_PACKAGE_TAIL, requestInfoClass,
                            msgBodyInfo.getName(), 0, msgBodyInfo.getMsgBodyMap());
                    if (subTopLevelClassList != null) {
                        topLevelClassList.addAll(subTopLevelClassList);
                    } else {
                        LOGGER.error("Get module api request param object info error, path({}),"
                                        + " method({}), module name({}),"
                                        + " param name({}), param location({}), param ref({}).",
                                urlPath, urlMethod, artifactId, requestParamInfo.getName(),
                                requestParamInfo.getIn(), ref);
                        return null;
                    }
                }
            }

            if (StringUtils.isEmpty(ref)) {
                String fieldTypeStr = null;
                if (MODULE_PARAM_LOCATION_BODY.equals(in) && YML_ELEMENT_APPLICATION_FILE.equals(consumeContentType)) {
                    fieldTypeStr = MultipartFile.class.getSimpleName();
                    requestInfoClass.addImportedType(MultipartFile.class.getTypeName());
                } else {
                    fieldTypeStr = JavaBeanGenerator.transferFieldType(type, format);
                }
                if (fieldTypeStr == null) {
                    LOGGER.error("Get module api request param info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param type({}).",
                            urlPath, urlMethod, artifactId, requestParamInfo.getName(),
                            requestParamInfo.getIn(), type);
                    return null;
                }

                Field field = null;
                if (defaultValue == null) {
                    field = JavaBeanGenerator.getField(requestParamInfo.getName(),
                            requestParamInfo.getDescription(), fieldTypeStr);
                } else {
                    field = JavaBeanGenerator.getField(requestParamInfo.getName(),
                            requestParamInfo.getDescription(), fieldTypeStr, defaultValue);
                }
                if (!field.getAnnotations().isEmpty()) {
                    requestInfoClass.addImportedType(JSONField.class.getTypeName());
                }
                field.addAnnotation(String.format(JAVA_FIELD_PARAM_LOCATION_ANNOUNCE_FROMAT,
                        requestParamInfo.getIn().toUpperCase()));
                requestInfoClass.addImportedType(ParamLocation.class.getTypeName());
                requestInfoClass.addImportedType(JAVA_IMPORT_TYPE_PARAM_LOCATION_TYPE);

                requestInfoClass.addField(field);

                Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(requestParamInfo.getName(), fieldTypeStr);
                requestInfoClass.addMethod(getterMethod);
                Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(requestParamInfo.getName(), fieldTypeStr);
                requestInfoClass.addMethod(setterMethod);

                if (enums != null) {

                    InnerEnum innerEnum = JavaBeanGenerator.getInnerEnum(requestParamInfo.getName(), fieldTypeStr);
                    for (String enumValue : enums) {
                        if (enumValue.indexOf(SHARP_FLAG) != -1) {
                            String[] st = enumValue.split(SHARP_FLAG);
                            if (st.length == ENUM_TYPE_PART_NUM) {
                                JavaBeanGenerator.addInnerEnumValue(innerEnum, st[1], st[0], fieldTypeStr);
                            }
                        } else {
                            JavaBeanGenerator.addInnerEnumValue(innerEnum, null, enumValue, fieldTypeStr);
                        }
                    }

                    requestInfoClass.addInnerEnum(innerEnum);
                }
            }
        }

        return topLevelClassList;

    }

    private void parseModuleMsgApiRequestPathParamInfo(List<Map<String, Object>> paramMapList,
                                                       Set<String> urlPathParamNameSet) {
        List<RequestParamInfo> requestParamInfoList =
                JSONArray.parseArray(JSON.toJSONString(paramMapList), RequestParamInfo.class);

        for (RequestParamInfo requestParamInfo : requestParamInfoList) {
            if (StringUtils.isEmpty(requestParamInfo.getName())
                    || StringUtils.isEmpty(requestParamInfo.getIn())
                    || !MODULE_PARAM_LOCATION_PATH.equalsIgnoreCase(requestParamInfo.getIn())) {
                continue;
            }
            urlPathParamNameSet.add(requestParamInfo.getName());
        }

        return;
    }

    private MsgBodyInfo parseModuleMsgApiRequestParamMapInfo(String ref) {
        MsgBodyInfo msgBodyInfo = new MsgBodyInfo();

        Map<String, Object> targetRequestBodyMap = findModuleMsgApiObjectMap(ref);
        if (targetRequestBodyMap == null) {
            return null;
        }

        String name = ref.substring(ref.lastIndexOf('/') + 1);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        msgBodyInfo.setName(name);
        msgBodyInfo.setMsgBodyMap(targetRequestBodyMap);
        return msgBodyInfo;
    }

    private MsgBodyInfo parseModuleMsgApiRequestBodyMapInfo(Map<String, Object> requestBodyMap) {
        MsgBodyInfo msgBodyInfo = null;

        Map<String, Object> targetRequestBodyMap = null;
        if (requestBodyMap != null) {
            String ref = (String) requestBodyMap.get(YML_ELEMENT_REF);
            if (ref != null) {
                targetRequestBodyMap = findModuleMsgApiObjectMap(ref);
                if (targetRequestBodyMap == null) {
                    return null;
                }

                String name = ref.substring(ref.lastIndexOf('/') + 1);
                if (StringUtils.isEmpty(name)) {
                    return null;
                }
                msgBodyInfo = new MsgBodyInfo();
                msgBodyInfo.setName(name);
                msgBodyInfo.setMsgBodyMap(targetRequestBodyMap);
            } else {
                targetRequestBodyMap = requestBodyMap;
            }

            if (apiVersion == OPEN_API_VERSION_3) {
                Map<String, Object> contentMap = (Map<String, Object>) targetRequestBodyMap.get(YML_ELEMENT_CONTENT);
                if (contentMap != null) {
                    Map<String, Object> typeMap = (Map<String, Object>) contentMap.get(YML_ELEMENT_APPLICATION_JSON);
                    if (typeMap != null) {
                        Map<String, Object> schemaMap = (Map<String, Object>) typeMap.get(YML_ELEMENT_SCHEMA);
                        if (schemaMap != null) {
                            msgBodyInfo = parseModuleMsgApiRequestBodyMapInfo(schemaMap);
                        }
                    } else {
                        typeMap = (Map<String, Object>) contentMap.get(YML_ELEMENT_APPLICATION_FILE);
                        if (typeMap != null) {
                            msgBodyInfo = new MsgBodyInfo();
                            msgBodyInfo.setName(YML_ELEMENT_APPLICATION_FILE);
                            msgBodyInfo.setMsgBodyMap(null);
                        }
                    }
                }
            }
        }
        return msgBodyInfo;
    }

    private MsgBodyInfo parseModuleMsgApiResponseBodyMapInfo(Map<Object, Object> responseBodyMap) {
        MsgBodyInfo msgBodyInfo = null;

        Map<String, Object> targetResponseBodyMap = null;
        if (responseBodyMap != null) {
            if (apiVersion == OPEN_API_VERSION_2) {
                Map<String, Object> httpStatusMap =
                        (Map<String, Object>) responseBodyMap.get(YML_ELEMENT_HTTP_STATUS_OK);
                if (httpStatusMap != null) {
                    Map<String, String> schemaMap = (Map<String, String>) httpStatusMap.get(YML_ELEMENT_SCHEMA);
                    if (schemaMap != null) {
                        String type = schemaMap.get(YML_ELEMENT_TYPE);
                        if (!StringUtils.isEmpty(type)
                                && List.class.getTypeName().equals(JavaBeanGenerator.transferFieldType(type, null))) {
                            return null;
                        }
                        String ref = schemaMap.get(YML_ELEMENT_REF);
                        targetResponseBodyMap = findModuleMsgApiObjectMap(ref);
                        if (targetResponseBodyMap == null) {
                            return null;
                        }

                        String name = ref.substring(ref.lastIndexOf('/') + 1);
                        if (StringUtils.isEmpty(name)) {
                            return null;
                        }
                        msgBodyInfo = new MsgBodyInfo();
                        msgBodyInfo.setName(name);
                        msgBodyInfo.setMsgBodyMap(targetResponseBodyMap);
                    }
                }
            } else {
                Map<String, Object> httpStatusMap
                        = (Map<String, Object>) responseBodyMap.get(YML_ELEMENT_HTTP_STATUS_OK);
                if (httpStatusMap == null) {
                    return null;
                }
                Map<String, Object> contentMap = (Map<String, Object>) httpStatusMap.get(YML_ELEMENT_CONTENT);
                if (contentMap == null) {
                    return null;
                }
                Map<String, Object> typeMap
                        = (Map<String, Object>) contentMap.get(YML_ELEMENT_APPLICATION_JSON);
                if (typeMap != null) {
                    Map<String, String> schemaMap = (Map<String, String>) typeMap.get(YML_ELEMENT_SCHEMA);
                    if (schemaMap != null) {
                        String ref = schemaMap.get(YML_ELEMENT_REF);
                        targetResponseBodyMap = findModuleMsgApiObjectMap(ref);
                        if (targetResponseBodyMap == null) {
                            return null;
                        }

                        String name = ref.substring(ref.lastIndexOf('/') + 1);
                        if (StringUtils.isEmpty(name)) {
                            return null;
                        }
                        msgBodyInfo = new MsgBodyInfo();
                        msgBodyInfo.setName(name);
                        msgBodyInfo.setMsgBodyMap(targetResponseBodyMap);
                    }
                }
            }
        }
        return msgBodyInfo;
    }

    private Map<String, Object> findModuleMsgApiObjectMap(String ref) {
        Map<String, Object> targetRequestBodyMap = null;

        if (!StringUtils.isEmpty(ref) && ref.startsWith("#")) {
            ref = ref.substring(1);
            StringTokenizer st = new StringTokenizer(ref, "/", false);
            targetRequestBodyMap = dataMap;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                targetRequestBodyMap = (Map<String, Object>) targetRequestBodyMap.get(token);
                if (targetRequestBodyMap == null) {
                    break;
                }
            }
            if (targetRequestBodyMap == null) {
                return null;
            }
        }
        return targetRequestBodyMap;
    }

    private List<TopLevelClass> parseModuleMsgApiRequestMsgBodyInfo(String urlPath, String urlMethod, String artifactId,
                                                                    String basePackage, String msgTail,
                                                                    TopLevelClass msgInfoClass,
                                                                    String msgInfoTypeName, int level,
                                                                    Map<String, Object> msgBodyMap) {
        List<TopLevelClass> topLevelClassList = new ArrayList<>();

        Map<String, Object> requestPropertyMap = (Map<String, Object>) msgBodyMap.get(YML_ELEMENT_PROPERTIES);
        if (requestPropertyMap == null) {
            LOGGER.error("Get module api msg info error, path({}), method({}), module name({}).",
                    urlPath, urlMethod, artifactId);
            return null;
        }

        String subClassType = basePackage + msgTail + msgInfoTypeName;

        TopLevelClass subRequestInfoClass
                = JavaBeanGenerator.getClazz(subClassType, null);

        for (Map.Entry<String, Object> requestPropertyEntry : requestPropertyMap.entrySet()) {
            String name = requestPropertyEntry.getKey();
            MsgPropertyInfo msgPropertyInfo
                    = JSONObject.parseObject(JSON.toJSONString(requestPropertyEntry.getValue()), MsgPropertyInfo.class);

            if (StringUtils.isEmpty(msgPropertyInfo.getType())) {
                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                + " param name({}), param location({}), param type({}).",
                        urlPath, urlMethod, artifactId, name,
                        MODULE_PARAM_LOCATION_BODY, msgPropertyInfo.getType());
                return null;
            }

            String fieldTypeStr = JavaBeanGenerator.transferFieldType(
                    msgPropertyInfo.getType(), msgPropertyInfo.getFormat());
            if (fieldTypeStr == null) {
                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                + " param name({}), param location({}), param type({}).",
                        urlPath, urlMethod, artifactId, name,
                        MODULE_PARAM_LOCATION_BODY, msgPropertyInfo.getType());
                return null;
            }

            if (List.class.getTypeName().equals(fieldTypeStr)) {
                if (msgPropertyInfo.getItems() != null) {

                    String subRequestInfoBeanName = null;
                    String type = msgPropertyInfo.getItems().get(YML_ELEMENT_TYPE);
                    if (!StringUtils.isEmpty(type)
                            && !Object.class.getTypeName().equals(JavaBeanGenerator.transferFieldType(type, null))) {

                        String javaType = JavaBeanGenerator.transferFieldType(type, null);
                        if (!StringUtils.isEmpty(javaType)) {
                            subRequestInfoBeanName = javaType.substring(javaType.lastIndexOf('.') + 1);
                        }
                    } else {
                        String ref = msgPropertyInfo.getItems().get(YML_ELEMENT_REF);
                        if (!StringUtils.isEmpty(ref)) {
                            subRequestInfoBeanName = ref.substring(ref.lastIndexOf('/') + 1);
                            if (StringUtils.isEmpty(subRequestInfoBeanName)) {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }

                            Map<String, Object> subRequestBodyMap = findModuleMsgApiObjectMap(ref);
                            if (subRequestBodyMap == null) {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }

                            List<TopLevelClass> subTopLevelClassList
                                    = parseModuleMsgApiRequestMsgBodyInfo(urlPath, urlMethod, artifactId,
                                    basePackage, msgTail, subRequestInfoClass, subRequestInfoBeanName,
                                    level + 1, subRequestBodyMap);
                            if (subTopLevelClassList != null) {
                                topLevelClassList.addAll(subTopLevelClassList);
                            } else {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }
                        }
                    }

                    if (subRequestInfoBeanName != null) {
                        fieldTypeStr = "List<" + subRequestInfoBeanName + ">";

                        Field field = JavaBeanGenerator.getField(name,
                                msgPropertyInfo.getDescription(), fieldTypeStr);
                        if (!field.getAnnotations().isEmpty()) {
                            subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                        }

                        subRequestInfoClass.addField(field);
                        subRequestInfoClass.addImportedType(List.class.getTypeName());

                        Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                        subRequestInfoClass.addMethod(getterMethod);
                        Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                        subRequestInfoClass.addMethod(setterMethod);
                    }

                }
            } else if (Object.class.getTypeName().equals(fieldTypeStr)) {

                String ref = ((Map<String, String>) requestPropertyEntry.getValue()).get(YML_ELEMENT_REF);

                String subRequestInfoBeanName = ref.substring(ref.lastIndexOf('/') + 1);
                if (StringUtils.isEmpty(subRequestInfoBeanName)) {
                    return null;
                }

                Map<String, Object> subRequestBodyMap = findModuleMsgApiObjectMap(ref);
                if (subRequestBodyMap == null) {
                    LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param ref({}).",
                            urlPath, urlMethod, artifactId, name,
                            MODULE_PARAM_LOCATION_BODY, ref);
                    return null;
                }

                List<TopLevelClass> subTopLevelClassList
                        = parseModuleMsgApiRequestMsgBodyInfo(urlPath, urlMethod, artifactId, basePackage, msgTail,
                        subRequestInfoClass, subRequestInfoBeanName, level + 1, subRequestBodyMap);
                if (subTopLevelClassList != null) {
                    topLevelClassList.addAll(subTopLevelClassList);
                } else {
                    LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param ref({}).",
                            urlPath, urlMethod, artifactId, name,
                            MODULE_PARAM_LOCATION_BODY, ref);
                    return null;
                }

                fieldTypeStr = basePackage + msgTail + subRequestInfoBeanName;

                Field field = JavaBeanGenerator.getField(name,
                        msgPropertyInfo.getDescription(), fieldTypeStr);
                if (!field.getAnnotations().isEmpty()) {
                    subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                }

                subRequestInfoClass.addField(field);

                Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(getterMethod);
                Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(setterMethod);

            } else {
                Field field = JavaBeanGenerator.getField(name,
                        msgPropertyInfo.getDescription(), fieldTypeStr);
                if (!field.getAnnotations().isEmpty()) {
                    subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                }

                subRequestInfoClass.addField(field);

                Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(getterMethod);
                Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(setterMethod);

                if (msgPropertyInfo.getEnumValueList() != null) {
                    List<String> enumValueList = msgPropertyInfo.getEnumValueList();

                    InnerEnum innerEnum = JavaBeanGenerator.getInnerEnum(name, fieldTypeStr);
                    for (String enumValue : enumValueList) {
                        if (enumValue.indexOf("#") != -1) {
                            String[] st = enumValue.split("#");
                            if (st.length == ENUM_TYPE_PART_NUM) {
                                JavaBeanGenerator.addInnerEnumValue(innerEnum, st[1], st[0], fieldTypeStr);
                            }
                        } else {
                            JavaBeanGenerator.addInnerEnumValue(innerEnum, null, enumValue, fieldTypeStr);
                        }
                    }

                    subRequestInfoClass.addInnerEnum(innerEnum);
                }
            }
        }

        if (level == 0) {
            Field field = JavaBeanGenerator.getField(JavaBeanGenerator.getCamelCaseString(msgInfoTypeName, false),
                    null, msgInfoTypeName);
            if (!field.getAnnotations().isEmpty()) {
                msgInfoClass.addImportedType(JSONField.class.getTypeName());
            }

            field.addAnnotation(String.format(JAVA_FIELD_PARAM_LOCATION_ANNOUNCE_FROMAT,
                    MODULE_PARAM_LOCATION_BODY.toUpperCase()));
            msgInfoClass.addImportedType(ParamLocation.class.getTypeName());
            msgInfoClass.addImportedType(JAVA_IMPORT_TYPE_PARAM_LOCATION_TYPE);

            msgInfoClass.addField(field);
            msgInfoClass.addImportedType(subClassType);

            Method getterMethod
                    = JavaBeanGenerator.getFieldGetterMethod(
                    JavaBeanGenerator.getCamelCaseString(msgInfoTypeName, false),
                    msgInfoTypeName);
            msgInfoClass.addMethod(getterMethod);
            Method setterMethod
                    = JavaBeanGenerator.getFieldSetterMethod(JavaBeanGenerator.getCamelCaseString(msgInfoTypeName,
                    false), msgInfoTypeName);
            msgInfoClass.addMethod(setterMethod);
        }

        topLevelClassList.add(subRequestInfoClass);

        return topLevelClassList;
    }

    private List<TopLevelClass> parseModuleMsgApiResponseMsgBodyInfo(String urlPath, String urlMethod,
                                                                     String artifactId, String basePackage,
                                                                     String msgInfoTypeName, int level,
                                                                     Map<String, Object> msgBodyMap) {
        List<TopLevelClass> topLevelClassList = new ArrayList<>();

        Map<String, Object> requestPropertyMap = (Map<String, Object>) msgBodyMap.get(YML_ELEMENT_PROPERTIES);
        if (requestPropertyMap == null) {
            LOGGER.error("Get module api msg info error, path({}), method({}), module name({}).",
                    urlPath, urlMethod, artifactId);
            return null;
        }

        String msgTail = MODULE_MSG_RESPONSE_PACKAGE_TAIL;
        if (level == 0) {
            msgTail = MODULE_MSG_RESPONSE_PACKAGE_TAIL;
        } else {
            msgTail = MODULE_MSG_RESPONSE_TYPE_PACKAGE_TAIL;
        }
        String subClassType = basePackage + msgTail + msgInfoTypeName;

        TopLevelClass subRequestInfoClass
                = JavaBeanGenerator.getClazz(subClassType, null);

        for (Map.Entry<String, Object> requestPropertyEntry : requestPropertyMap.entrySet()) {
            String name = requestPropertyEntry.getKey();
            MsgPropertyInfo msgPropertyInfo
                    = JSONObject.parseObject(JSON.toJSONString(requestPropertyEntry.getValue()), MsgPropertyInfo.class);

            if (StringUtils.isEmpty(msgPropertyInfo.getType())) {
                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                + " param name({}), param location({}), param type({}).",
                        urlPath, urlMethod, artifactId, name,
                        MODULE_PARAM_LOCATION_BODY, msgPropertyInfo.getType());
                return null;
            }

            String fieldTypeStr = JavaBeanGenerator.transferFieldType(
                    msgPropertyInfo.getType(), msgPropertyInfo.getFormat());
            if (fieldTypeStr == null) {
                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                + " param name({}), param location({}), param type({}).",
                        urlPath, urlMethod, artifactId, name,
                        MODULE_PARAM_LOCATION_BODY, msgPropertyInfo.getType());
                return null;
            }

            if (List.class.getTypeName().equals(fieldTypeStr)) {
                if (msgPropertyInfo.getItems() != null) {

                    String subRequestInfoBeanName = null;
                    String type = msgPropertyInfo.getItems().get(YML_ELEMENT_TYPE);
                    if (!StringUtils.isEmpty(type)
                            && !Object.class.getTypeName().equals(JavaBeanGenerator.transferFieldType(type, null))) {

                        String javaType = JavaBeanGenerator.transferFieldType(type, null);
                        if (!StringUtils.isEmpty(javaType)) {
                            subRequestInfoBeanName = javaType.substring(javaType.lastIndexOf('.') + 1);
                        }
                    } else {
                        String ref = msgPropertyInfo.getItems().get(YML_ELEMENT_REF);
                        if (!StringUtils.isEmpty(ref)) {
                            subRequestInfoBeanName = ref.substring(ref.lastIndexOf('/') + 1);
                            if (StringUtils.isEmpty(subRequestInfoBeanName)) {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }

                            Map<String, Object> subRequestBodyMap = findModuleMsgApiObjectMap(ref);
                            if (subRequestBodyMap == null) {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }

                            List<TopLevelClass> subTopLevelClassList
                                    = parseModuleMsgApiResponseMsgBodyInfo(urlPath, urlMethod, artifactId, basePackage,
                                    subRequestInfoBeanName, level + 1, subRequestBodyMap);
                            if (subTopLevelClassList != null) {
                                topLevelClassList.addAll(subTopLevelClassList);
                            } else {
                                LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                                + " param name({}), param location({}), param ref({}).",
                                        urlPath, urlMethod, artifactId, name,
                                        MODULE_PARAM_LOCATION_BODY, ref);
                                return null;
                            }
                        }
                    }

                    if (subRequestInfoBeanName != null) {
                        fieldTypeStr = "List<" + subRequestInfoBeanName + ">";

                        Field field = JavaBeanGenerator.getField(name,
                                msgPropertyInfo.getDescription(), fieldTypeStr);
                        if (!field.getAnnotations().isEmpty()) {
                            subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                        }

                        subRequestInfoClass.addField(field);
                        subRequestInfoClass.addImportedType(List.class.getTypeName());

                        if (level == 0) {
                            subRequestInfoClass.addImportedType(basePackage
                                    + MODULE_MSG_RESPONSE_TYPE_PACKAGE_TAIL + subRequestInfoBeanName);
                        }

                        Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                        subRequestInfoClass.addMethod(getterMethod);
                        Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                        subRequestInfoClass.addMethod(setterMethod);
                    }

                }
            } else if (Object.class.getTypeName().equals(fieldTypeStr)) {

                String ref = ((Map<String, String>) requestPropertyEntry.getValue()).get(YML_ELEMENT_REF);

                String subRequestInfoBeanName = ref.substring(ref.lastIndexOf('/') + 1);
                if (StringUtils.isEmpty(subRequestInfoBeanName)) {
                    return null;
                }

                Map<String, Object> subRequestBodyMap = findModuleMsgApiObjectMap(ref);
                if (subRequestBodyMap == null) {
                    LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param ref({}).",
                            urlPath, urlMethod, artifactId, name,
                            MODULE_PARAM_LOCATION_BODY, ref);
                    return null;
                }

                List<TopLevelClass> subTopLevelClassList
                        = parseModuleMsgApiResponseMsgBodyInfo(urlPath, urlMethod, artifactId, basePackage,
                        subRequestInfoBeanName, level + 1, subRequestBodyMap);
                if (subTopLevelClassList != null) {
                    topLevelClassList.addAll(subTopLevelClassList);
                } else {
                    LOGGER.error("Get module api msg info error, path({}), method({}), module name({}),"
                                    + " param name({}), param location({}), param ref({}).",
                            urlPath, urlMethod, artifactId, name,
                            MODULE_PARAM_LOCATION_BODY, ref);
                    return null;
                }

                fieldTypeStr = basePackage + MODULE_MSG_RESPONSE_TYPE_PACKAGE_TAIL + subRequestInfoBeanName;

                Field field = JavaBeanGenerator.getField(name,
                        msgPropertyInfo.getDescription(), fieldTypeStr);
                if (!field.getAnnotations().isEmpty()) {
                    subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                }

                subRequestInfoClass.addField(field);
                if (level == 0) {
                    subRequestInfoClass.addImportedType(fieldTypeStr);
                }

                Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(getterMethod);
                Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(setterMethod);

            } else {
                Field field = null;
                if (msgPropertyInfo.getDefaultValue() == null) {
                    field = JavaBeanGenerator.getField(name,
                            msgPropertyInfo.getDescription(), fieldTypeStr);
                } else {
                    field = JavaBeanGenerator.getField(name,
                            msgPropertyInfo.getDescription(), fieldTypeStr, msgPropertyInfo.getDefaultValue());
                }
                if (!field.getAnnotations().isEmpty()) {
                    subRequestInfoClass.addImportedType(JSONField.class.getTypeName());
                }

                subRequestInfoClass.addField(field);

                Method getterMethod = JavaBeanGenerator.getFieldGetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(getterMethod);
                Method setterMethod = JavaBeanGenerator.getFieldSetterMethod(name, fieldTypeStr);
                subRequestInfoClass.addMethod(setterMethod);

                if (msgPropertyInfo.getEnumValueList() != null) {
                    List<String> enumValueList = msgPropertyInfo.getEnumValueList();

                    InnerEnum innerEnum = JavaBeanGenerator.getInnerEnum(name, fieldTypeStr);
                    for (String enumValue : enumValueList) {
                        if (enumValue.indexOf("#") != -1) {
                            String[] st = enumValue.split("#");
                            if (st.length == ENUM_TYPE_PART_NUM) {
                                JavaBeanGenerator.addInnerEnumValue(innerEnum, st[1], st[0], fieldTypeStr);
                            }
                        } else {
                            JavaBeanGenerator.addInnerEnumValue(innerEnum, null, enumValue, fieldTypeStr);
                        }
                    }

                    subRequestInfoClass.addInnerEnum(innerEnum);
                }
            }
        }

        topLevelClassList.add(subRequestInfoClass);

        return topLevelClassList;
    }

    private TopLevelClass parseModuleMsgApiServiceInfo(String type, String basePackage, String serviceName,
                                                       String serviceImplName, String serviceDesc) {
        TopLevelClass serviceClass
                = JavaBeanGenerator.getClazz(basePackage + MODULE_SERVICE_PACKAGE_TAIL
                + serviceImplName, serviceDesc);
        serviceClass.addAnnotation(String.format(JAVA_CLASS_COMPONENT_ANNOUNCE_FORMAT, serviceName));
        serviceClass.addImportedType(Component.class.getTypeName());

        serviceClass.addImportedType(Logger.class.getTypeName());
        serviceClass.addImportedType(LoggerFactory.class.getTypeName());

        FullyQualifiedJavaType fieldType = new FullyQualifiedJavaType(Logger.class.getSimpleName());
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fieldType);
        field.setName(Logger.class.getSimpleName().toUpperCase());
        field.setFinal(true);
        field.setStatic(true);
        field.setInitializationString(String.format("LoggerFactory.getLogger(%s.class)", serviceImplName));
        serviceClass.addField(field);

        if (SERVICE_TYPE_HTTP.equals(type)) {
            FullyQualifiedJavaType interfaceType
                    = new FullyQualifiedJavaType(IComponentsHttpService.class.getTypeName());
            serviceClass.addSuperInterface(interfaceType);
            serviceClass.addImportedType(IComponentsHttpService.class.getTypeName());

            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(HttpResponseInfo.class.getTypeName());

            Method method = new Method();
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(returnType);
            method.setName("execute");

            Parameter parameter = new Parameter(new FullyQualifiedJavaType(HttpRequestInfo.class.getTypeName()),
                    JavaBeanGenerator.getCamelCaseString(HttpRequestInfo.class.getSimpleName(), false));
            method.addParameter(0, parameter);

            method.addAnnotation(JAVA_METHOD_OVERRIDE_ANNOUNCE);

            FullyQualifiedJavaType exceptionType = new FullyQualifiedJavaType(HttpException.class.getSimpleName());
            method.addException(exceptionType);

            serviceClass.addImportedType(HttpResponseInfo.class.getTypeName());
            serviceClass.addImportedType(HttpRequestInfo.class.getTypeName());
            serviceClass.addImportedType(HttpException.class.getTypeName());

            method.addBodyLine("HttpResponseInfo httpResponseInfo = new HttpResponseInfo();\n");
            method.addBodyLine("return httpResponseInfo;");

            serviceClass.addMethod(method);
        } else if (SERVICE_TYPE_WS.equals(type)) {
            FullyQualifiedJavaType interfaceType = new FullyQualifiedJavaType(IComponentsWsService.class.getTypeName());
            serviceClass.addSuperInterface(interfaceType);
            serviceClass.addImportedType(IComponentsWsService.class.getTypeName());

            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(WsResponseInfo.class.getTypeName());

            Method method = new Method();
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(returnType);
            method.setName("execute");

            Parameter parameter = new Parameter(new FullyQualifiedJavaType(WsRequestInfo.class.getTypeName()),
                    JavaBeanGenerator.getCamelCaseString(WsRequestInfo.class.getSimpleName(), false));
            method.addParameter(0, parameter);

            method.addAnnotation(JAVA_METHOD_OVERRIDE_ANNOUNCE);

            serviceClass.addImportedType(WsResponseInfo.class.getTypeName());
            serviceClass.addImportedType(WsRequestInfo.class.getTypeName());

            method.addBodyLine("return null;");

            serviceClass.addMethod(method);
        } else if (SERVICE_TYPE_MSG.equals(type)) {
            FullyQualifiedJavaType interfaceType
                    = new FullyQualifiedJavaType(IComponentsMsgService.class.getTypeName());
            serviceClass.addSuperInterface(interfaceType);
            serviceClass.addImportedType(IComponentsMsgService.class.getTypeName());

            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(boolean.class.getTypeName());

            Method method = new Method();
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(returnType);
            method.setName("execute");

            Parameter parameter = new Parameter(new FullyQualifiedJavaType(long.class.getTypeName()), "bornTimestamp");
            method.addParameter(0, parameter);
            parameter = new Parameter(new FullyQualifiedJavaType(MsgRequestInfo.class.getTypeName()),
                    JavaBeanGenerator.getCamelCaseString(MsgRequestInfo.class.getSimpleName(), false));
            method.addParameter(1, parameter);

            method.addAnnotation(JAVA_METHOD_OVERRIDE_ANNOUNCE);

            serviceClass.addImportedType(MsgRequestInfo.class.getTypeName());

            method.addBodyLine("return true;");

            serviceClass.addMethod(method);
        }
        return serviceClass;
    }

    private void parseModuleMsgApiControllerMethodInfo(TopLevelClass controllerClass,
                                                       String serviceClassName,
                                                       String requestClassName,
                                                       String responseClassName,
                                                       String urlPath, String urlMethod, String operationId,
                                                       String consumeContentType, Set<String> urlPathParamNameSet) {

        String methodName = JavaBeanGenerator.getCamelCaseString(operationId, false);
        String urlMethodListStr = urlMethod.toUpperCase();

        controllerClass.addImportedType(Map.class.getTypeName());
        controllerClass.addImportedType(HashMap.class.getTypeName());

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(JAVA_METHOD_RETURN_CONTROLLER);
        controllerClass.addImportedType(DeferredResult.class.getTypeName());

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(returnType);
        method.setName(methodName);

        method.addAnnotation(JAVA_CLASS_CROSS_ORIGIN_ANNOUNCE);
        method.addAnnotation(String.format(JAVA_METHOD_REQUEST_MAPPING_ANNOUNCE, urlPath, urlMethodListStr));
        method.addJavaDocLine(String.format(MODULE_CONTROLLER_MATHOD_DOC_FROMAT,
                serviceClassName, requestClassName, responseClassName));
        controllerClass.addImportedType(JAVA_IMPORT_TYPE_CROSS_ORIGIN);

        int index = 0;
        Parameter parameterRequest = new Parameter(
                new FullyQualifiedJavaType(HttpServletRequest.class.getSimpleName()), "request");
        method.addParameter(index, parameterRequest);
        controllerClass.addImportedType(HttpServletRequest.class.getTypeName());

        Parameter parameterResponse = new Parameter(
                new FullyQualifiedJavaType(HttpServletResponse.class.getSimpleName()), "response");
        method.addParameter(++index, parameterResponse);
        controllerClass.addImportedType(HttpServletResponse.class.getTypeName());

        if (YML_ELEMENT_APPLICATION_JSON.equals(consumeContentType)
                && (SERVICE_METHOD_HTTP_POST.equalsIgnoreCase(urlMethod)
                || SERVICE_METHOD_HTTP_PUT.equalsIgnoreCase(urlMethod))) {
            Parameter parameterRequestJson = new Parameter(
                    new FullyQualifiedJavaType(JSONObject.class.getSimpleName()), "requestJson");
            parameterRequestJson.addAnnotation(JAVA_METHOD_REQUEST_BODY_ANNOUNCE);
            method.addParameter(++index, parameterRequestJson);
            controllerClass.addImportedType(JSONObject.class.getTypeName());
        }

        if (YML_ELEMENT_APPLICATION_FILE.equals(consumeContentType)) {
            Parameter parameterRequestFile = new Parameter(
                    new FullyQualifiedJavaType(MultipartFile.class.getSimpleName()), "requestFile");
            parameterRequestFile.addAnnotation(JAVA_METHOD_REQUEST_FILE_ANNOUNCE);
            method.addParameter(++index, parameterRequestFile);
            controllerClass.addImportedType(MultipartFile.class.getTypeName());
        }

        method.addBodyLine("Map<String, String> pathParamMap = new HashMap<>(COLLECTION_INIT_SIZE);");
        controllerClass.addStaticImport(CommonDefine.class.getTypeName() + JAVA_IMPORT_TYPE_STAR_TAIL);
        controllerClass.addStaticImport(RequestMethod.class.getTypeName() + JAVA_IMPORT_TYPE_STAR_TAIL);

        for (String pathParamName : urlPathParamNameSet) {
            String camelPathParamName = JavaBeanGenerator.getCamelCaseString(pathParamName, false);
            Parameter parameter = new Parameter(
                    new FullyQualifiedJavaType(String.class.getTypeName()), camelPathParamName);
            parameter.addAnnotation(
                    String.format(JAVA_METHOD_PATH_VARIABLE_ANNOUNCE_FORMAT, pathParamName));
            method.addParameter(++index, parameter);

            method.addBodyLine(String.format("pathParamMap.put(\"%s\", %s);", pathParamName, camelPathParamName));
        }
        method.addBodyLine(String.format("String url = \"%s\";", urlPath));
        if (YML_ELEMENT_APPLICATION_JSON.equals(consumeContentType)) {
            if (SERVICE_METHOD_HTTP_POST.equalsIgnoreCase(urlMethod)
                    || SERVICE_METHOD_HTTP_PUT.equalsIgnoreCase(urlMethod)) {
                method.addBodyLine("return httpControllerTools.deal(url, request, response, "
                        + "pathParamMap, (Object)requestJson);");
            } else {
                method.addBodyLine("return httpControllerTools.deal(url, request, response, "
                        + "pathParamMap, null);");
            }
        }

        if (YML_ELEMENT_APPLICATION_FILE.equals(consumeContentType)) {
            method.addBodyLine("return httpControllerTools.deal(url, request, response, "
                    + "pathParamMap, (Object)requestFile);");
        }

        FullyQualifiedJavaType exceptionType = new FullyQualifiedJavaType(HttpException.class.getSimpleName());
        method.addException(exceptionType);
        controllerClass.addImportedType(HttpException.class.getTypeName());

        controllerClass.addMethod(method);
    }


    private boolean generatorModuleBeanService(String moduleSrcPath, List<TopLevelClass> clazzList) {

        for (TopLevelClass clazz : clazzList) {
            AbstractGeneratedJavaFile generatedJavaFile = new AbstractGeneratedJavaFile(clazz, moduleSrcPath, null);
            try {
                JavaBeanGenerator.writeGeneratedJavaFile(generatedJavaFile, recoverExist);
            } catch (Exception e) {
                LOGGER.error("Generate module msg service info error, error info({}).", formatExceptionInfo(e));
                return false;
            }
        }
        return true;
    }

    private boolean generatorModuleConfig(String moduleResourcePath, Document configDoc) {

        String filePath = moduleResourcePath + File.separator + MODULE_CONFIG_FILE_NAME;

        if (!new File(filePath).exists() || recoverExist) {
            try {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding(DEFAULT_ENCODING);
                FileOutputStream out = new FileOutputStream(filePath);
                XMLWriter writer = new XMLWriter(out, format);
                writer.write(configDoc);
                writer.close();
            } catch (Exception e) {
                LOGGER.error("Generate module config info error, error info({}).", formatExceptionInfo(e));
                return false;
            }
        }

        return true;
    }

    private boolean generatorModulePom(String modulePath, String artifactId) {

        String modulePomPath = modulePath + File.separator + POM_FILE_NAME;
        try {
            String pomContent = pomTemplateContent.replace("MODULE_GROUP_ID", groupId);
            pomContent = pomContent.replace("MODULE_ARTIFACT_ID", artifactId);
            pomContent = pomContent.replace("MODULE_PARENT_GROUP_ID", parentGroupId);
            pomContent = pomContent.replace("MODULE_PARENT_ARTIFACT_ID", parentArtifactId);
            if (!new File(modulePomPath).exists() || recoverExist) {
                FileUtils.writeFileByStr(modulePomPath, pomContent, false);
            }
        } catch (Exception e) {
            LOGGER.error("Generate module pom info error, error info({}).", formatExceptionInfo(e));
            return false;
        }

        return true;
    }
}



