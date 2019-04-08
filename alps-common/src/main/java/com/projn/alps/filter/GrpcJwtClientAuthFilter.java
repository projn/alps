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

package com.projn.alps.filter;

import io.grpc.*;

import static com.projn.alps.define.HttpDefine.HEADER_JWT_TOKEN;
import static com.projn.alps.define.HttpDefine.JWT_TOKEN_PREFIX;
import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * grpc jwt client auth filter impl
 *
 * @author : sunyuecheng
 */
public class GrpcJwtClientAuthFilter implements ClientInterceptor {

    private String token;

    public GrpcJwtClientAuthFilter(String token) {
        this.token = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions,
            Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(Metadata.Key.of(HEADER_JWT_TOKEN, ASCII_STRING_MARSHALLER), JWT_TOKEN_PREFIX + token);
                super.start(responseListener, headers);
            }
        };
    }
}
