package com.projn.alps.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

/**
 * mybatis config
 *
 * @author : sunyuecheng
 */
@Configuration
@ImportResource("classpath:bootstrap/spring-mybatis.xml")
//@PropertySource(value = {"file:${config.dir}/config/mybatis.properties"})
@ConditionalOnProperty(name = "system.bean.switch.mybatis", havingValue = "true", matchIfMissing=true)
public class MybatisConfig {

//    @Value("${mybatis.configLocation}")
//    private String configLocation;
//
//    @Value("${mybatis.mapperLocations.module}")
//    private String mapperLocationsModule;
//
//    @Value("${mybatis.mapperLocations.local}")
//    private String mapperLocationsLocal;
//
//    @Value("${mybatis.basePackage}")
//    private String basePackage;
//
//    @Autowired
//    private DruidDataSource druidDataSource;
//
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(druidDataSource);
//        sqlSessionFactoryBean.setConfigLocation(
//                new PathMatchingResourcePatternResolver().getResource(configLocation));
//        sqlSessionFactoryBean.setMapperLocations(
//                new PathMatchingResourcePatternResolver().getResources(mapperLocationsModule));
//        return sqlSessionFactoryBean;
//    }
//
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setBasePackage(basePackage);
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("SqlSessionFactoryBean");
//
//        return mapperScannerConfigurer;
//    }
}
