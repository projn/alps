package com.projn.alps.alpsmicroservice.indicator;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.util.CounterUtils;
import com.projn.alps.widget.WsSessionInfoMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * service health indicator
 *
 * @author : sunyuecheng
 */
@Component("service")
public class ServiceHealthIndicator implements HealthIndicator {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * health
     *
     * @return org.springframework.boot.actuate.health.Health :
     */
    @Override
    public Health health() {
        if (threadPoolTaskExecutor.getActiveCount() < threadPoolTaskExecutor.getMaxPoolSize()) {
            return Health.up().withDetail(MicroServiceDefine.AGENT_ONLINE_NUM_KEY, WsSessionInfoMap.getInstance().
                    getPoolSize()).
                    withDetail(MicroServiceDefine.MSG_DEAL_STATUS_KEY, JSON.toJSONString(CounterUtils.getCounterMap())).
                    withDetail(MicroServiceDefine.SERVICE_THREAD_POOL_USED_COUNT_KEY,
                            threadPoolTaskExecutor.getActiveCount()).build();
        } else {
            return Health.status(MicroServiceDefine.SERVICE_STATUS_BUSY).
                    withDetail(MicroServiceDefine.AGENT_ONLINE_NUM_KEY, WsSessionInfoMap.getInstance().
                            getPoolSize()).
                    withDetail(MicroServiceDefine.MSG_DEAL_STATUS_KEY, JSON.toJSONString(CounterUtils.getCounterMap())).
                    withDetail(MicroServiceDefine.SERVICE_THREAD_POOL_USED_COUNT_KEY,
                            threadPoolTaskExecutor.getActiveCount()).build();
        }
    }
}
