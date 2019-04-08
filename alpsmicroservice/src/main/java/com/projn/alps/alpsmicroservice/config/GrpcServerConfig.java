package com.projn.alps.alpsmicroservice.config;

import com.projn.alps.alpsmicroservice.controller.RpcController;
import com.projn.alps.alpsmicroservice.filter.GrpcJwtServerAuthFilter;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.rpc.GrpcRequestMsgInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.JwtTokenUtils;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import io.grpc.*;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.jsonwebtoken.Claims;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import static com.projn.alps.define.HttpDefine.*;
import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

@Configuration
@PropertySource("classpath:config/grpc-server.properties")
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

    @Bean("grpcServer")
    Server grpcServer() throws Exception {
        ServerBuilder builder = ServerBuilder.forPort(port)
                .addService(new RpcController());

        Server server = builder.intercept(new GrpcJwtServerAuthFilter(compression, jwtAuth)).build().start();

        return server;
    }

}
