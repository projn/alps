package com.projn.alps.initialize;

import com.projn.alps.struct.MasterInfo;
import com.projn.alps.struct.MqConsumerInfo;
import com.projn.alps.struct.RequestServiceInfo;

import java.util.List;
import java.util.Map;

/**
 * service data
 *
 * @author : sunyuecheng
 */
public final class ServiceData {

    private static MasterInfo masterInfo;

    private static Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap = null;

    private static Map<String, MqConsumerInfo> mqConsumerInfoMap = null;

    public static MasterInfo getMasterInfo() {
        return masterInfo;
    }

    public static String jwtSecretKey;

    public static void setMasterInfo(MasterInfo masterInfo) {
        ServiceData.masterInfo = masterInfo;
    }

    public static Map<String, Map<String, List<RequestServiceInfo>>> getRequestServiceInfoMap() {
        return requestServiceInfoMap;
    }

    public static void setRequestServiceInfoMap(
            Map<String, Map<String, List<RequestServiceInfo>>> requestServiceInfoMap) {
        ServiceData.requestServiceInfoMap = requestServiceInfoMap;
    }

    public static Map<String, MqConsumerInfo> getMqConsumerInfoMap() {
        return mqConsumerInfoMap;
    }

    public static void setMqConsumerInfoMap(Map<String, MqConsumerInfo> mqConsumerInfoMap) {
        ServiceData.mqConsumerInfoMap = mqConsumerInfoMap;
    }

    public static String getJwtSecretKey() {
        return jwtSecretKey;
    }

    public static void setJwtSecretKey(String jwtSecretKey) {
        ServiceData.jwtSecretKey = jwtSecretKey;
    }

    private ServiceData() {
    }
}
