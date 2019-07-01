/*
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.projn.alps.alpsmicroservice.filter;

import com.projn.alps.initialize.ServiceData;
import com.projn.alps.rpc.GrpcRequestMsgInfo;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.util.JwtTokenUtils;
import io.grpc.*;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.JWT_CLAIM_ROLE_INFO_KEY;
import static com.projn.alps.define.HttpDefine.HEADER_JWT_TOKEN;
import static com.projn.alps.define.HttpDefine.JWT_TOKEN_PREFIX;
import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * grpc jwt server auth filter
 *
 * @author : sunyuecheng
 */
@Component
@ConditionalOnProperty(name = "system.bean.switch.rpc.auth", havingValue = "true", matchIfMissing = true)
public class GrpcJwtServerAuthFilter implements ServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcJwtServerAuthFilter.class);

    private String compression = null;

    private boolean jwtAuth = false;

    /**
     * grpc jwt server auth filter
     *
     * @param compression :
     * @param jwtAuth     :
     */
    public GrpcJwtServerAuthFilter(String compression, boolean jwtAuth) {
        this.compression = compression;
        this.jwtAuth = jwtAuth;
    }

    /**
     * intercept call
     *
     * @param serverCall        :
     * @param metadata          :
     * @param serverCallHandler :
     * @return ServerCall.Listener<ReqT> :
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String authToken = null;
        if (jwtAuth) {
            String authHeader = metadata.get(Metadata.Key.of(HEADER_JWT_TOKEN, ASCII_STRING_MARSHALLER));
            if (authHeader != null && authHeader.startsWith(JWT_TOKEN_PREFIX)) {
                authToken = authHeader.substring(JWT_TOKEN_PREFIX.length());
            } else {
                LOGGER.error("Invaild requset info.");
                return null;
            }
        }

        if (!StringUtils.isEmpty(compression)) {
            serverCall.setCompression(compression);
        }

        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);

        if (authToken == null) {
            return listener;
        } else {
            Claims claims = JwtTokenUtils.parseToken(authToken, ServiceData.getJwtSecretKey());
            String role = (String) claims.get(JWT_CLAIM_ROLE_INFO_KEY);

            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
                @Override
                public void onMessage(ReqT message) {

                    if (message.getClass().equals(GrpcRequestMsgInfo.class)) {

                        GrpcRequestMsgInfo grpcRequestMsgInfo = (GrpcRequestMsgInfo) message;

                        String serviceName = grpcRequestMsgInfo.getServiceName();

                        Map<String, List<RequestServiceInfo>> requestServiceInfoMap
                                = ServiceData.getRequestServiceInfoMap().get(serviceName);

                        if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()
                                || requestServiceInfoMap.get(RequestServiceInfo.SERVICE_TYPE_RPC) == null) {
                            LOGGER.error("Invaild request service info, service name (" + serviceName + ").");
                            this.onCancel();
                            return;
                        }

                        List<RequestServiceInfo> requestServiceInfoList
                                = requestServiceInfoMap.get(RequestServiceInfo.SERVICE_TYPE_RPC);
                        if (requestServiceInfoList.size() != 1) {
                            LOGGER.error("Invaild request service info, service name (" + serviceName + ").");
                            this.onCancel();
                            return;
                        }
                        RequestServiceInfo requestServiceInfo = requestServiceInfoList.get(0);

                        if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_RPC)) {
                            LOGGER.error("Invaild request service type info, type("
                                    + requestServiceInfo.getType() + ").");
                            this.onCancel();
                            return;
                        }

                        if (requestServiceInfo.getUserRoleNameList() != null
                                && !StringUtils.isEmpty(role)
                                && !requestServiceInfo.getUserRoleNameList().contains(role)) {
                            LOGGER.error("This role has no access rights.");
                            this.onCancel();
                            return;
                        }

                        super.onMessage(message);
                    } else {
                        this.onCancel();
                    }
                }
            };
        }

    }

}
