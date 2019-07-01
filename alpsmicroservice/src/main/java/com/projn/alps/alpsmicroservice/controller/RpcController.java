package com.projn.alps.alpsmicroservice.controller;

import com.projn.alps.rpc.GrpcRequestMsgInfo;
import com.projn.alps.rpc.GrpcResponseMsgInfo;
import com.projn.alps.rpc.GrpcServiceGrpc;
import com.projn.alps.tool.RpcControllerTools;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * rpc controller
 *
 * @author : sunyuecheng
 */
public class RpcController extends GrpcServiceGrpc.GrpcServiceImplBase {

    @Autowired
    private RpcControllerTools rpcControllerTools;

    /**
     * execute
     *
     * @param request          :
     * @param responseObserver :
     */
    @Override
    public void execute(GrpcRequestMsgInfo request, StreamObserver<GrpcResponseMsgInfo> responseObserver) {
        rpcControllerTools.deal(request, responseObserver);
    }
}
