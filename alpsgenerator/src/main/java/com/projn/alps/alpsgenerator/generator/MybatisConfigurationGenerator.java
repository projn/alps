package com.projn.alps.alpsgenerator.generator;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.config.*;

/**
 * mybatis configuration generator
 *
 * @author : sunyuecheng
 */
public class MybatisConfigurationGenerator {
    private static final String DEFAULT_CONTEXT_ID = "mysqlTables";
    private static final String DEFAULT_CONTEXT_TARGET_RUNTIME = "MyBatis3";
    private static final String SUPPRESS_ALL_COMMENTS_KEY = "suppressAllComments";
    private static final String SUPPRESS_DATE_KEY = "suppressDate";
    private static final String FORCE_BIG_DECIMALS_KEY = "forceBigDecimals";
    private static final String ENABLE_SUB_PACKAGES_KEY = "enableSubPackages";
    private static final String TRIM_STRINGS_KEY = "trimStrings";
    private static final String CONFIGURATION_TYPE_XML = "XMLMAPPER";

    private Configuration configuration;
    private Context context;

    /**
     * mybatis configuration generator
     */
    public MybatisConfigurationGenerator() {
        initContext();
    }

    private void initContext() {
        configuration = new Configuration();

        ModelType mt = null;
        context = new Context(mt);
        context.setId(DEFAULT_CONTEXT_ID);
        context.setTargetRuntime(DEFAULT_CONTEXT_TARGET_RUNTIME);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        commentGeneratorConfiguration.addProperty(SUPPRESS_ALL_COMMENTS_KEY, Boolean.TRUE.toString());
        commentGeneratorConfiguration.addProperty(SUPPRESS_DATE_KEY, Boolean.TRUE.toString());

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        javaTypeResolverConfiguration.addProperty(FORCE_BIG_DECIMALS_KEY, Boolean.FALSE.toString());

        configuration.addContext(context);
    }

    /**
     * set jdbc configuration
     *
     * @param driverClass   :
     * @param connectionUrl :
     * @param userName      :
     * @param password      :
     * @return boolean :
     */
    public boolean setJdbcConfiguration(String driverClass, String connectionUrl, String userName, String password) {
        if (StringUtils.isEmpty(driverClass) || StringUtils.isEmpty(connectionUrl)
                || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return false;
        }

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionUrl);
        jdbcConnectionConfiguration.setUserId(userName);
        jdbcConnectionConfiguration.setPassword(password);

        return true;
    }

    /**
     * set java model configuration
     *
     * @param targetPackage :
     * @param targetProject :
     * @return boolean :
     */
    public boolean setJavaModelConfiguration(String targetPackage, String targetProject) {
        if (StringUtils.isEmpty(targetPackage) || StringUtils.isEmpty(targetProject)) {
            return false;
        }

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        javaModelGeneratorConfiguration.addProperty(ENABLE_SUB_PACKAGES_KEY, Boolean.TRUE.toString());
        javaModelGeneratorConfiguration.addProperty(TRIM_STRINGS_KEY, Boolean.FALSE.toString());

        return true;
    }

    /**
     * set sql map configuration
     *
     * @param targetPackage :
     * @param targetProject :
     * @return boolean :
     */
    public boolean setSqlMapConfiguration(String targetPackage, String targetProject) {
        if (StringUtils.isEmpty(targetPackage) || StringUtils.isEmpty(targetProject)) {
            return false;
        }

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();

        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);

        sqlMapGeneratorConfiguration.addProperty(ENABLE_SUB_PACKAGES_KEY, Boolean.TRUE.toString());

        return true;
    }

    /**
     * set java client configuration
     *
     * @param targetPackage :
     * @param targetProject :
     * @return boolean :
     */
    public boolean setJavaClientConfiguration(String targetPackage, String targetProject) {
        if (StringUtils.isEmpty(targetPackage) || StringUtils.isEmpty(targetProject)) {
            return false;
        }

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        javaClientGeneratorConfiguration.setConfigurationType(CONFIGURATION_TYPE_XML);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);

        javaClientGeneratorConfiguration.addProperty(ENABLE_SUB_PACKAGES_KEY, Boolean.TRUE.toString());

        return true;
    }

    /**
     * add table configuration
     *
     * @param schema           :
     * @param tableName        :
     * @param domainObjectName :
     * @param mapperName       :
     * @return boolean :
     */
    public boolean addTableConfiguration(String schema, String tableName,
                                         String domainObjectName, String mapperName) {
        if (StringUtils.isEmpty(schema) || StringUtils.isEmpty(tableName)
                || StringUtils.isEmpty(domainObjectName) || StringUtils.isEmpty(mapperName)) {
            return false;
        }

        TableConfiguration tableConfiguration = new TableConfiguration(context);

        context.addTableConfiguration(tableConfiguration);

        tableConfiguration.setSchema(schema);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setDomainObjectName(domainObjectName);
        tableConfiguration.setMapperName(mapperName);

        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setSelectByExampleQueryId(Boolean.FALSE.toString());

        return true;
    }

    /**
     * get configuration
     *
     * @return Configuration :
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
