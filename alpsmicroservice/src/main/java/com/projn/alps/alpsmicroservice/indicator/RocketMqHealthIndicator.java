package com.projn.alps.alpsmicroservice.indicator;

import com.alibaba.fastjson.JSON;
import com.projn.alps.alpsmicroservice.define.MicroServiceDefine;
import com.projn.alps.alpsmicroservice.property.RocketMqProperties;
import com.projn.alps.alpsmicroservice.struct.RocketMqGroupConsumeInfo;
import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.protocol.body.ConsumerConnection;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.command.SubCommandException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * rocketmq health indicator
 *
 * @author : sunyuecheng
 */
@Component("rocketmq")
public class RocketMqHealthIndicator implements HealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqHealthIndicator.class);

    @Autowired
    private RocketMqProperties rocketMqProperties;

    /**
     * health
     *
     * @return org.springframework.boot.actuate.health.Health :
     */
    @Override
    public Health health() {
        List<RocketMqGroupConsumeInfo> rocketMqGroupConsumeInfoList = null;
        try {
            rocketMqGroupConsumeInfoList = execute(rocketMqProperties.getQueueServerAddress());
        } catch (Exception e) {
            LOGGER.error("Get rocketmq group comsume info error, error info({}).", formatExceptionInfo(e));
            return Health.down().withDetail(MicroServiceDefine.ROCKET_MQ_CONSUME_STATUS_KEY, null).build();
        }

        for (RocketMqGroupConsumeInfo rocketMqGroupConsumeInfo : rocketMqGroupConsumeInfoList) {
            if (rocketMqGroupConsumeInfo.getDiffTotal() > rocketMqGroupConsumeInfo.getDiffTotal()) {
                return Health.down().withDetail(MicroServiceDefine.ROCKET_MQ_CONSUME_STATUS_KEY,
                        JSON.toJSONString(rocketMqGroupConsumeInfoList)).build();
            }
        }
        return Health.up().withDetail(MicroServiceDefine.ROCKET_MQ_CONSUME_STATUS_KEY,
                JSON.toJSONString(rocketMqGroupConsumeInfoList)).build();
    }

    private List<RocketMqGroupConsumeInfo> execute(String namesrvAddr) throws SubCommandException {
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, namesrvAddr);

        List<RocketMqGroupConsumeInfo> groupConsumeInfoList = new ArrayList<>();

        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));

        try {
            defaultMQAdminExt.start();

            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
            for (String topic : topicList.getTopicList()) {
                if (topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                    String consumerGroup = topic.substring(MixAll.RETRY_GROUP_TOPIC_PREFIX.length());
                    try {
                        ConsumeStats consumeStats = null;
                        try {
                            consumeStats = defaultMQAdminExt.examineConsumeStats(consumerGroup);
                        } catch (Exception e) {
                            LOGGER.warn("examineConsumeStats exception, " + consumerGroup, e);
                        }

                        ConsumerConnection cc = null;
                        try {
                            cc = defaultMQAdminExt.examineConsumerConnectionInfo(consumerGroup);
                        } catch (Exception e) {
                            LOGGER.warn("examineConsumerConnectionInfo exception, " + consumerGroup, e);
                        }

                        GroupConsumeInfo groupConsumeInfo = new GroupConsumeInfo();
                        groupConsumeInfo.setGroup(consumerGroup);

                        if (consumeStats != null) {
                            groupConsumeInfo.setConsumeTps((int) consumeStats.getConsumeTps());
                            groupConsumeInfo.setDiffTotal(consumeStats.computeTotalDiff());
                        }

                        if (cc != null) {
                            groupConsumeInfo.setCount(cc.getConnectionSet().size());
                            groupConsumeInfo.setMessageModel(cc.getMessageModel());
                            groupConsumeInfo.setConsumeType(cc.getConsumeType());
                            groupConsumeInfo.setVersion(cc.computeMinVersion());
                        }

                        RocketMqGroupConsumeInfo rocketMqGroupConsumeInfo = new RocketMqGroupConsumeInfo();
                        rocketMqGroupConsumeInfo.setGroup(groupConsumeInfo.getGroup());
                        rocketMqGroupConsumeInfo.setCount(groupConsumeInfo.getCount());
                        rocketMqGroupConsumeInfo.setConsumeTypeDesc(groupConsumeInfo.consumeTypeDesc());
                        rocketMqGroupConsumeInfo.setMessageModelDesc(groupConsumeInfo.messageModelDesc());
                        rocketMqGroupConsumeInfo.setConsumeTps(groupConsumeInfo.getConsumeTps());
                        rocketMqGroupConsumeInfo.setDiffTotal(groupConsumeInfo.getDiffTotal());

                        groupConsumeInfoList.add(rocketMqGroupConsumeInfo);
                    } catch (Exception e) {
                        LOGGER.warn("examineConsumeStats or examineConsumerConnectionInfo exception, "
                                + consumerGroup, e);
                    }
                }
            }
        } catch (Exception e) {
            throw new SubCommandException(this.getClass().getSimpleName() + " command failed", e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return groupConsumeInfoList;
    }

    /**
     * group comsume info
     *
     * @author : sunyuecheng
     */
    private static class GroupConsumeInfo {
        private String group;
        private int version;
        private int count;
        private ConsumeType consumeType;
        private MessageModel messageModel;
        private int consumeTps;
        private long diffTotal;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String consumeTypeDesc() {
            if (this.count != 0) {
                return this.getConsumeType() == ConsumeType.CONSUME_ACTIVELY ? "PULL" : "PUSH";
            }
            return "";
        }

        public ConsumeType getConsumeType() {
            return consumeType;
        }

        public void setConsumeType(ConsumeType consumeType) {
            this.consumeType = consumeType;
        }

        public String messageModelDesc() {
            if (this.count != 0 && this.getConsumeType() == ConsumeType.CONSUME_PASSIVELY) {
                return this.getMessageModel().toString();
            }
            return "";
        }

        public MessageModel getMessageModel() {
            return messageModel;
        }

        public void setMessageModel(MessageModel messageModel) {
            this.messageModel = messageModel;
        }

        public String versionDesc() {
            if (this.count != 0) {
                return MQVersion.getVersionDesc(this.version);
            }
            return "";
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getDiffTotal() {
            return diffTotal;
        }

        public void setDiffTotal(long diffTotal) {
            this.diffTotal = diffTotal;
        }


        public int getConsumeTps() {
            return consumeTps;
        }

        public void setConsumeTps(int consumeTps) {
            this.consumeTps = consumeTps;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
