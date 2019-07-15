package com.projn.alps.bean;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

/**
 * druid data source config
 *
 * @author : sunyuecheng
 */
@Configuration
@PropertySource(value = "file:${config.dir}/config/druid.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.mybatis", havingValue = "true", matchIfMissing = true)
public class DruidDataSourceConfig {

    @Value("${druid.name}")
    private String name = null;

    @Value("${druid.url}")
    private String url = null;

    @Value("${druid.username}")
    private String username = null;

    @Value("${druid.password}")
    private String password = null;

    @Value("${druid.testWhileIdle}")
    private Boolean testWhileIdle = false;

    @Value("${druid.testOnBorrow}")
    private Boolean testOnBorrow = true;

    @Value("${druid.validationQuery}")
    private String validationQuery = "SELECT 1";

    @Value("${druid.useGlobalDataSourceStat}")
    private Boolean useGlobalDataSourceStat = false;

    @Value("${druid.asyncInit}")
    private Boolean asyncInit = false;

    @Value("${druid.filters}")
    private String filters = null;

    //@Value("${druid.timeBetweenLogStatsMillis}")
    //private Long timeBetweenLogStatsMillis = null;

    //@Value("${druid.stat.sql.MaxSize}")
    //private String maxSize = "1000";

    //@Value("${druid.clearFiltersEnable}")
    //private Boolean clearFiltersEnable = true;

    //@Value("${druid.resetStatEnable}")
    //private Boolean resetStatEnable = true;

    //@Value("${druid.notFullTimeoutRetryCount}")
    //private String notFullTimeoutRetryCount = "0";

    @Value("${druid.timeBetweenEvictionRunsMillis}")
    private String timeBetweenEvictionRunsMillis = "60000";

    //@Value("${druid.maxWaitThreadCount}")
    //private String maxWaitThreadCount = "-1";

    @Value("${druid.failFast}")
    private Boolean failFast = false;

    //@Value("${druid.phyTimeoutMillis}")
    //private String phyTimeoutMillis = "-1";

    @Value("${druid.minEvictableIdleTimeMillis}")
    private String minEvictableIdleTimeMillis = "30000";

    @Value("${druid.maxEvictableIdleTimeMillis}")
    private String maxEvictableIdleTimeMillis = "30000";

    @Value("${druid.keepAlive}")
    private Boolean keepAlive = false;

    @Value("${druid.poolPreparedStatements}")
    private Boolean poolPreparedStatements = false;

    //@Value("${druid.initVariants}")
    //private Boolean initVariants = false;

    //@Value("${druid.initGlobalVariants}")
    //private Boolean initGlobalVariants = false;

    //@Value("${druid.useUnfairLock}")
    //private Boolean useUnfairLock = null;

    @Value("${druid.driverClassName}")
    private String driverClassName = null;

    @Value("${druid.initialSize}")
    private String initialSize = "0";

    @Value("${druid.minIdle}")
    private String minIdle = "0";

    @Value("${druid.maxActive}")
    private String maxActive = "60000";

    @Value("${druid.maxWait}")
    private String maxWait = null;

    //@Value("${druid.killWhenSocketReadTimeout}")
    //private Boolean killWhenSocketReadTimeout = false;

    @Value("${druid.connectProperties}")
    private String connectProperties = null;

    @Value("${druid.maxPoolPreparedStatementPerConnectionSize}")
    private String maxPoolPreparedStatementPerConnectionSize = "10";

    private Properties toProperties() throws Exception {
        Properties properties = new Properties();
        notNullAdd(properties, "druid.name", this.name);
        notNullAdd(properties, "druid.url", this.url);
        notNullAdd(properties, "druid.username", this.username);
        notNullAdd(properties, "druid.password", this.password);
        notNullAdd(properties, "druid.testWhileIdle", this.testWhileIdle);
        notNullAdd(properties, "druid.testOnBorrow", this.testOnBorrow);
        notNullAdd(properties, "druid.validationQuery", this.validationQuery);
        notNullAdd(properties, "druid.useGlobalDataSourceStat", this.useGlobalDataSourceStat);
        notNullAdd(properties, "druid.asyncInit", this.asyncInit);
        notNullAdd(properties, "druid.filters", this.filters);
        //notNullAdd(properties, "druid.timeBetweenLogStatsMillis", this.timeBetweenLogStatsMillis);
        //notNullAdd(properties, "druid.stat.sql.MaxSize", this.maxSize);
        //notNullAdd(properties, "druid.clearFiltersEnable", this.clearFiltersEnable);
        //notNullAdd(properties, "druid.resetStatEnable", this.resetStatEnable);
        //notNullAdd(properties, "druid.notFullTimeoutRetryCount", this.notFullTimeoutRetryCount);
        notNullAdd(properties, "druid.timeBetweenEvictionRunsMillis", this.timeBetweenEvictionRunsMillis);
        //notNullAdd(properties, "druid.maxWaitThreadCount", this.maxWaitThreadCount);
        notNullAdd(properties, "druid.failFast", this.failFast);
        //notNullAdd(properties, "druid.phyTimeoutMillis", this.phyTimeoutMillis);
        notNullAdd(properties, "druid.minEvictableIdleTimeMillis", this.minEvictableIdleTimeMillis);
        notNullAdd(properties, "druid.maxEvictableIdleTimeMillis", this.maxEvictableIdleTimeMillis);
        notNullAdd(properties, "druid.keepAlive", this.keepAlive);
        notNullAdd(properties, "druid.poolPreparedStatements", this.poolPreparedStatements);
        //notNullAdd(properties, "druid.initVariants", this.initVariants);
        //notNullAdd(properties, "druid.initGlobalVariants", this.initGlobalVariants);
        //notNullAdd(properties, "druid.useUnfairLock", this.useUnfairLock);
        notNullAdd(properties, "druid.driverClassName", this.driverClassName);
        notNullAdd(properties, "druid.initialSize", this.initialSize);
        notNullAdd(properties, "druid.minIdle", this.minIdle);
        notNullAdd(properties, "druid.maxActive", this.maxActive);
        notNullAdd(properties, "druid.maxWait", this.maxWait);
        //notNullAdd(properties, "druid.killWhenSocketReadTimeout", this.killWhenSocketReadTimeout);
        notNullAdd(properties, "druid.connectProperties", this.connectProperties);
        notNullAdd(properties, "druid.maxPoolPreparedStatementPerConnectionSize",
                this.maxPoolPreparedStatementPerConnectionSize);
        return properties;
    }

    private void notNullAdd(Properties properties, String key, Object value) {
        if (value != null) {
            properties.setProperty(key, value.toString());
        }
    }

    /**
     * druid data source
     *
     * @return com.alibaba.druid.pool.DruidDataSource :
     * @throws Exception :
     */
    @Bean
    public DruidDataSource druidDataSource() throws Exception {
        Properties properties = toProperties();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.configFromPropety(properties);
        return druidDataSource;
    }
}
