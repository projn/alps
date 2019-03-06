package com.projn.alps.tool;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import static com.projn.alps.util.CommonUtils.formatExceptionInfo;

/**
 * quartz manager
 *
 * @author : sunyuecheng
 */
@Component
public class QuartzJobTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzJobTools.class);

    @Autowired
    private Scheduler scheduler;

    /**
     * add job
     *
     * @param jobClass         :
     * @param jobName          :
     * @param jobGroupName     :
     * @param triggerName      :
     * @param triggerGroupName :
     * @param cronExpression   :
     * @param jobPropertiesMap :
     * @return boolean :
     */
    public boolean addJob(Class<? extends Job> jobClass, String jobName, String jobGroupName,
                          String triggerName, String triggerGroupName, String cronExpression,
                          Map<String, String> jobPropertiesMap) {
        if (jobClass == null || StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName)
                || StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(triggerGroupName)
                || StringUtils.isEmpty(cronExpression) || jobPropertiesMap == null) {
            LOGGER.error("Error param.");
            return false;
        }

        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName).setJobData(new JobDataMap(jobPropertiesMap)).build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, triggerGroupName)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            LOGGER.error("Add job error, error info({}).", formatExceptionInfo(e));
            return false;
        }
        return true;
    }

    /**
     * boolean modify job time
     *
     * @param jobName          :
     * @param jobGroupName     :
     * @param triggerName      :
     * @param triggerGroupName :
     * @param cronExpression   :
     * @return :
     */
    public boolean modifyJobTime(String jobName, String jobGroupName,
                                 String triggerName, String triggerGroupName, String cronExpression) {
        if (StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName) || StringUtils.isEmpty(triggerName)
                || StringUtils.isEmpty(triggerGroupName) || StringUtils.isEmpty(cronExpression)) {
            LOGGER.error("Error param.");
            return false;
        }
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return false;
            }

            String oldCronExpression = trigger.getCronExpression();
            if (!oldCronExpression.equalsIgnoreCase(cronExpression)) {

                JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));

                Trigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobName, triggerGroupName)
                        .startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();

                scheduler.scheduleJob(jobDetail, newTrigger);

                if (!scheduler.isShutdown()) {
                    scheduler.start();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Modify job time error, error info({}).", formatExceptionInfo(e));
            return false;
        }
        return true;
    }

    /**
     * remove job
     *
     * @param jobName          :
     * @param jobGroupName     :
     * @param triggerName      :
     * @param triggerGroupName :
     */
    public void removeJob(String jobName, String jobGroupName,
                          String triggerName, String triggerGroupName) {
        if (StringUtils.isEmpty(jobName) || StringUtils.isEmpty(triggerName)) {
            LOGGER.error("Error param.");
            return;
        }
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {
            LOGGER.error("Reomve job error, error info({}).", formatExceptionInfo(e));
        }
    }

    /**
     * start jobs
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error("Start jobs error, error info({}).", formatExceptionInfo(e));
        }
    }

    /**
     * shutdown jobs
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            LOGGER.error("Shutdown jobs error, error info(" + formatExceptionInfo(e) + ").");
        }
    }

    /**
     * fill job params
     *
     * @param obj        :
     * @param jobDataMap :
     * @throws Exception :
     */
    public static void fillJobParams(Object obj, JobDataMap jobDataMap) throws Exception {

        if (obj != null && !jobDataMap.isEmpty()) {

            for (Map.Entry<String, Object> propertyEntry : jobDataMap.entrySet()) {
                String fieldName = propertyEntry.getKey();
                PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
                Method setMethod = pd.getWriteMethod();

                Class type = obj.getClass().getDeclaredField(fieldName).getType();
                if (Integer.class.equals(type) || int.class.equals(type)) {
                    Integer integerVal = jobDataMap.getIntValue(fieldName);
                    setMethod.invoke(obj, integerVal);
                } else if (Long.class.equals(type) || long.class.equals(type)) {
                    Long longVal = jobDataMap.getLongValue(fieldName);
                    setMethod.invoke(obj, longVal);
                } else if (Short.class.equals(type) || short.class.equals(type)) {
                    setMethod.invoke(obj, Short.valueOf(jobDataMap.getString(fieldName)));
                } else if (Float.class.equals(type) || float.class.equals(type)) {
                    Float floatVal = jobDataMap.getFloatValue(fieldName);
                    setMethod.invoke(obj, floatVal);
                } else if (Double.class.equals(type) || double.class.equals(type)) {
                    Double doubleVal = jobDataMap.getDoubleValue(fieldName);
                    setMethod.invoke(obj, doubleVal);
                } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                    Boolean booleanVal = jobDataMap.getBooleanValue(fieldName);
                    setMethod.invoke(obj, booleanVal);
                } else if (String.class.equals(type)) {
                    setMethod.invoke(obj, jobDataMap.getString(fieldName));
                } else {
                    throw new Exception("Unsupport value type,field name("
                            + fieldName + "),type(" + type.getName() + ").");
                }
            }
        }
    }

}