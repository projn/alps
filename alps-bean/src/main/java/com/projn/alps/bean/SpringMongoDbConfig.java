package com.projn.alps.bean;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * spring mongo db config
 *
 * @author : sunyuecheng
 */
@Configuration
@PropertySource(value = "file:${config.dir}/config/mongodb.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.mongodb", havingValue = "true", matchIfMissing = true)
public class SpringMongoDbConfig {

    @Value("${mongo.host}")
    private String host;
    @Value("${mongo.port}")
    private int port;
    @Value("${mongo.dbName}")
    private String dbName;
    @Value("${mongo.username}")
    private String username;
    @Value("${mongo.password}")
    private String password;

    @Value("${mongo.description}")
    private String description;
    @Value("${mongo.applicationName}")
    private String applicationName;
    @Value("${mongo.writeConcern}")
    private String writeConcern = "ACKNOWLEDGED";
    @Value("${mongo.readConcern}")
    private String readConcern = "DEFAULT";

    @Value("${mongo.minConnectionsPerHost}")
    private int minConnectionsPerHost;
    @Value("${mongo.maxConnectionsPerHost}")
    private int maxConnectionsPerHost = 100;
    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier}")
    private int threadsAllowedToBlockForConnectionMultiplier = 5;
    @Value("${mongo.serverSelectionTimeout}")
    private int serverSelectionTimeout = 1000 * 30;
    @Value("${mongo.maxWaitTime}")
    private int maxWaitTime;
    @Value("${mongo.maxConnectionIdleTime}")
    private int maxConnectionIdleTime;
    @Value("${mongo.maxConnectionLifeTime}")
    private int maxConnectionLifeTime;

    @Value("${mongo.connectTimeout}")
    private int connectTimeout = 1000 * 10;
    @Value("${mongo.socketTimeout}")
    private int socketTimeout = 0;
    @Value("${mongo.socketKeepAlive}")
    private boolean socketKeepAlive = false;

    @Value("${mongo.alwaysUseMBeans}")
    private boolean alwaysUseMBeans = false;
    @Value("${mongo.heartbeatFrequency}")
    private int heartbeatFrequency = 10000;
    @Value("${mongo.minHeartbeatFrequency}")
    private int minHeartbeatFrequency = 500;
    @Value("${mongo.heartbeatConnectTimeout}")
    private int heartbeatConnectTimeout = 20000;
    @Value("${mongo.heartbeatSocketTimeout}")
    private int heartbeatSocketTimeout = 20000;
    @Value("${mongo.localThreshold}")
    private int localThreshold = 15;

    @Value("${mongo.requiredReplicaSetName}")
    private String requiredReplicaSetName;

    /**
     * mongo client options
     *
     * @return MongoClientOptions :
     */
    @Bean
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder()
                .writeConcern(WriteConcern.valueOf(writeConcern))
                .readConcern(new ReadConcern(ReadConcernLevel.fromString(readConcern)))
                .connectionsPerHost(maxConnectionsPerHost)
                .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
                .serverSelectionTimeout(serverSelectionTimeout)
                .maxWaitTime(maxWaitTime)
                .maxConnectionIdleTime(maxConnectionIdleTime)
                .maxConnectionLifeTime(maxConnectionLifeTime)
                .connectTimeout(connectTimeout)
                .socketTimeout(socketTimeout)
                .alwaysUseMBeans(Boolean.valueOf(alwaysUseMBeans))
                .heartbeatFrequency(heartbeatFrequency)
                .heartbeatConnectTimeout(heartbeatConnectTimeout)
                .heartbeatSocketTimeout(heartbeatSocketTimeout)
                .localThreshold(localThreshold)
                .requiredReplicaSetName(requiredReplicaSetName).build();
    }

    /**
     * mongo client
     *
     * @return MongoClient :
     */
    @Bean
    public MongoClient mongoClient() {
        ServerAddress serverAddress = new ServerAddress(host, port);
        List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
        serverAddressList.add(serverAddress);

        MongoCredential credential
                = MongoCredential.createScramSha1Credential(username, dbName, password.toCharArray());

        MongoClient mongoClient = new MongoClient(serverAddressList, credential, mongoClientOptions());
        return mongoClient;
    }

    /**
     * mongo template
     *
     * @param :
     * @return MongoTemplate :
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient(), dbName);
        MongoTemplate mongoTemplate = new MongoTemplate(simpleMongoDbFactory);
        return mongoTemplate;
    }

}
