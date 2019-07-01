package com.projn.alps.alpsmicroservice.config;

import com.projn.alps.alpsmicroservice.controller.RpcController;
import com.projn.alps.alpsmicroservice.filter.GrpcJwtServerAuthFilter;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * grpc server config
 *
 * @author : sunyuecheng
 */
@Configuration
@PropertySource(value = "file:${config.dir}/onfig/grpc-server.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(name = "system.bean.switch.grpc", havingValue = "true", matchIfMissing = true)
public class GrpcServerConfig {

    @Value("${grpc.server.host}")
    private String host;

    @Value("${grpc.server.port}")
    private int port = 0;

    @Value("${grpc.server.compression}")
    private String compression = null;

    @Value("${grpc.server.jwt.auth}")
    private boolean jwtAuth = false;

    /**
     * grpc server
     *
     * @return Server :
     * @throws Exception :
     */
    @Bean("grpcServer")
    public Server grpcServer() throws Exception {
        ServerBuilder builder = ServerBuilder.forPort(port)
                .addService(new RpcController());

        Server server = builder.intercept(new GrpcJwtServerAuthFilter(compression, jwtAuth)).build().start();

        return server;
    }

}
