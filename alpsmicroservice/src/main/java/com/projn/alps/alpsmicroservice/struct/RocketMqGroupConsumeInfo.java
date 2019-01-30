package com.projn.alps.alpsmicroservice.struct;


/**
 * rocket mq group consume info
 *
 * @author : sunyuecheng
 */
public class RocketMqGroupConsumeInfo {

    private String group;
    private int count;
    private String consumeTypeDesc;
    private String messageModelDesc;
    private int consumeTps;
    private long diffTotal;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getConsumeTypeDesc() {
        return consumeTypeDesc;
    }

    public void setConsumeTypeDesc(String consumeTypeDesc) {
        this.consumeTypeDesc = consumeTypeDesc;
    }

    public String getMessageModelDesc() {
        return messageModelDesc;
    }

    public void setMessageModelDesc(String messageModelDesc) {
        this.messageModelDesc = messageModelDesc;
    }

    public int getConsumeTps() {
        return consumeTps;
    }

    public void setConsumeTps(int consumeTps) {
        this.consumeTps = consumeTps;
    }

    public long getDiffTotal() {
        return diffTotal;
    }

    public void setDiffTotal(long diffTotal) {
        this.diffTotal = diffTotal;
    }
}
