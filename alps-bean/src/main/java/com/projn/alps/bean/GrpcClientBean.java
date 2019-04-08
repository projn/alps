package com.projn.alps.bean;

import com.projn.alps.msg.request.RpcRequestMsgInfo;
import com.projn.alps.msg.response.RpcResponseMsgInfo;
import com.projn.alps.rpc.GrpcRequestMsgInfo;
import com.projn.alps.rpc.GrpcResponseMsgInfo;
import com.projn.alps.rpc.GrpcServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GrpcClientBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClientBean.class);

    private static final int DEFAULT_TIMEOUT_SENCONDS = 10;

    private ManagedChannel channel;

    private GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;

    private String host;

    private int port;

    private int timeoutSeconds = DEFAULT_TIMEOUT_SENCONDS;

    private String compression;

    public GrpcClientBean(String host, int port, int timeoutSeconds,
                          SslContext sslContext, String compression) {
        this.host = host;
        this.port = port;
        this.timeoutSeconds = timeoutSeconds;
        this.compression = compression;


        this.channel = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(sslContext)
                .build();

        this.blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
    }

    public GrpcClientBean(String host, int port, int timeoutSeconds,
                          ClientInterceptor clientInterceptor, String compression) throws Exception {
        this.host = host;
        this.port = port;
        this.timeoutSeconds = timeoutSeconds;
        this.compression = compression;

        ManagedChannelBuilder builder = ManagedChannelBuilder.forAddress(host, port)
                .keepAliveWithoutCalls(false);

        this.channel = clientInterceptor == null
                ? builder.build()
                : builder.intercept(clientInterceptor).build();

        this.blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public RpcResponseMsgInfo execute(RpcRequestMsgInfo rpcRequestMsgInfo) throws Exception {
        GrpcRequestMsgInfo grpcRequestMsgInfo =
                GrpcRequestMsgInfo.newBuilder()
                        .setServiceName(rpcRequestMsgInfo.getServiceName())
                        .setRequestBody(rpcRequestMsgInfo.getRequestBody())
                        .build();

        GrpcResponseMsgInfo grpcResponseMsgInfo = StringUtils.isEmpty(compression)
                ? blockingStub.execute(grpcRequestMsgInfo)
                : blockingStub.withCompression(compression)
                .withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS).execute(grpcRequestMsgInfo);

        if (grpcResponseMsgInfo != null) {
            RpcResponseMsgInfo rpcResponseMsgInfo = new RpcResponseMsgInfo();
            rpcResponseMsgInfo.setResponseBody(grpcResponseMsgInfo.getResponseBody());

            return rpcResponseMsgInfo;
        }
        return null;
    }
}
