package com.projn.alps.tool;

import com.alibaba.fastjson.JSON;
import com.projn.alps.initialize.InitializeBean;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.msg.request.RpcRequestMsgInfo;
import com.projn.alps.rpc.GrpcRequestMsgInfo;
import com.projn.alps.rpc.GrpcResponseMsgInfo;
import com.projn.alps.service.IComponentsRpcService;
import com.projn.alps.struct.RequestServiceInfo;
import com.projn.alps.struct.RpcRequestInfo;
import com.projn.alps.struct.RpcResponseInfo;
import com.projn.alps.util.ParamCheckUtils;
import com.projn.alps.util.RequestInfoUtils;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.projn.alps.define.HttpDefine.HTTP_METHOD_POST;
import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * http controller tools
 *
 * @author : sunyuecheng
 */
@Component
public class RpcControllerTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcControllerTools.class);

    /**
     * deal
     *
     * @param request          :
     * @param responseObserver :
     */
    public void deal(GrpcRequestMsgInfo request, StreamObserver<GrpcResponseMsgInfo> responseObserver) {
        if (request == null || responseObserver == null) {
            LOGGER.error("Invaild param.");
            responseObserver.onError(new Exception("Invaild param."));
            responseObserver.onCompleted();
            return;
        }

        RpcRequestMsgInfo rpcRequestMsgInfo = new RpcRequestMsgInfo();
        rpcRequestMsgInfo.setServiceName(request.getServiceName());
        rpcRequestMsgInfo.setRequestBody(request.getRequestBody());

        String uri = rpcRequestMsgInfo.getServiceName();

        LOGGER.info("Request url({}).", uri);

        String serviceName = null;
        Map<String, RequestServiceInfo> requestServiceInfoMap = ServiceData.getRequestServiceInfoMap().get(uri);
        if (requestServiceInfoMap == null || requestServiceInfoMap.isEmpty()) {
            LOGGER.error("Invaild request service info, service name (" + uri + ").");
            responseObserver.onError(new Exception("Invaild request service info."));
            responseObserver.onCompleted();
            return;
        }
        RequestServiceInfo requestServiceInfo
                = requestServiceInfoMap.get(HTTP_METHOD_POST.toLowerCase());
        if (!requestServiceInfo.getType().equalsIgnoreCase(RequestServiceInfo.SERVICE_TYPE_RPC)) {
            LOGGER.error("Invaild request service type info, type(" + requestServiceInfo.getType() + ").");
            responseObserver.onError(new Exception("Invaild request service info."));
            responseObserver.onCompleted();
            return;
        }

        RpcRequestInfo rpcRequestInfo = null;
        if (requestServiceInfo.getParamClass() != null) {

            try {
                rpcRequestInfo = RequestInfoUtils.convertRpcRequestInfo(
                        rpcRequestMsgInfo.getRequestBody(), requestServiceInfo.getParamClass());
            } catch (Exception e) {
                LOGGER.error("Convert request info error,error info(" + e.getMessage() + ").");
                responseObserver.onError(new Exception("Invaild request service info."));
                responseObserver.onCompleted();
                return;
            }

            if (rpcRequestInfo != null && rpcRequestInfo.getParamObj() != null) {
                try {
                    ParamCheckUtils.checkParam(rpcRequestInfo.getParamObj());
                } catch (Exception e) {
                    LOGGER.error("Check param error,error info(" + e.getMessage() + ").");
                    responseObserver.onError(new Exception("Invaild request service info."));
                    responseObserver.onCompleted();
                    return;
                }
            }
        }

        if (StringUtils.isEmpty(serviceName) || rpcRequestInfo == null) {
            LOGGER.error("Invaild request info error,service name({}), request info({}).",
                    serviceName, JSON.toJSONString(rpcRequestInfo));
            responseObserver.onError(new Exception("Invaild request info error."));
            responseObserver.onCompleted();
            return;
        }

        try {
            IComponentsRpcService bean = InitializeBean.getBean(serviceName);
            if (bean == null) {
                LOGGER.error("Invaild service name error,service name({}).", serviceName);
                responseObserver.onError(new Exception("Invaild service name error."));
                responseObserver.onCompleted();
                return;
            } else {
                RpcResponseInfo rpcResponseInfo = bean.execute(rpcRequestInfo);

                GrpcResponseMsgInfo grpcResponseMsgInfo = GrpcResponseMsgInfo
                        .newBuilder()
                        .setResponseBody(JSON.toJSONString(rpcResponseInfo.getMsg()))
                        .build();
                responseObserver.onNext(grpcResponseMsgInfo);
                responseObserver.onCompleted();

            }
        } catch (Exception e) {
            LOGGER.error("Deal request info error ,error info({}).", formatExceptionInfo(e));
            responseObserver.onError(e);
            responseObserver.onCompleted();
            return;
        }

    }

}