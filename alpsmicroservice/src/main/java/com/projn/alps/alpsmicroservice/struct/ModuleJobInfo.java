package com.projn.alps.alpsmicroservice.struct;

import java.util.Map;

/**
 * module job info
 *
 * @author : sunyuecheng
 */
public class ModuleJobInfo {
    private static final String JOB_GROUP_NAME_HEADER = "group";
    private static final String JOB_TRIGGER_NAME_HEADER = "trigger";
    private static final String JOB_TRIGGER_GROUP_NAME_HEADER = "triggerGroup";

    private String jobClassName = null;

    private String jobName = null;

    private String jobGroupName = null;

    private String triggerName = null;

    private String triggerGroupName = null;

    private String cronExpression = null;

    private Map<String, String> jobPropertiesMap = null;

    /**
     * module job info
     */
    public ModuleJobInfo() {
    }

    /**
     * module job info
     *
     * @param jobClassName     :
     * @param jobName          :
     * @param cronExpression   :
     * @param jobPropertiesMap :
     */
    public ModuleJobInfo(String jobClassName, String jobName, String cronExpression,
                         Map<String, String> jobPropertiesMap) {
        this.jobClassName = jobClassName;
        this.jobName = jobName;
        this.jobGroupName = JOB_GROUP_NAME_HEADER + jobName;
        this.triggerName = JOB_TRIGGER_NAME_HEADER + jobName;
        this.triggerGroupName = JOB_TRIGGER_GROUP_NAME_HEADER + jobName;
        this.cronExpression = cronExpression;
        this.jobPropertiesMap = jobPropertiesMap;
    }

    /**
     * module job info
     *
     * @param jobClassName     :
     * @param jobName          :
     * @param jobGroupName     :
     * @param triggerName      :
     * @param triggerGroupName :
     * @param cronExpression   :
     * @param jobPropertiesMap :
     */
    public ModuleJobInfo(String jobClassName, String jobName, String jobGroupName,
                         String triggerName, String triggerGroupName, String cronExpression,
                         Map<String, String> jobPropertiesMap) {
        this.jobClassName = jobClassName;
        this.jobName = jobName;
        this.jobGroupName = jobGroupName;
        this.triggerName = triggerName;
        this.triggerGroupName = triggerGroupName;
        this.cronExpression = cronExpression;
        this.jobPropertiesMap = jobPropertiesMap;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroupName() {
        return triggerGroupName;
    }

    public void setTriggerGroupName(String triggerGroupName) {
        this.triggerGroupName = triggerGroupName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<String, String> getJobPropertiesMap() {
        return jobPropertiesMap;
    }

    public void setJobPropertiesMap(Map<String, String> jobPropertiesMap) {
        this.jobPropertiesMap = jobPropertiesMap;
    }
}