package com.projn.alps.alpsmicroservice.define;

/**
 * access server define
 *
 * @author : sunyuecheng
 */
public final class MicroServiceDefine {

    public static final String AGENT_ID_KEY = "AGENT_ID";

    /**
     * default websocket session timeout interval minutes
     */
    public static final int DEFAULT_WEBSOCKET_SESSION_TIMEOUT_INTERVAL_MINUTES = 30;

    /**
     * default max websocket session count
     */
    public static final int DEFAULT_MAX_WEBSOCKET_SESSION_COUNT = 1000;

    /**
     * http api service send msg
     */
    public static final String HTTP_API_SERVICE_SEND_MSG = "sendMsg";

    /**
     * http api service send msg uri
     */
    public static final String HTTP_API_SERVICE_SEND_MSG_URI = "/api/sendMsg";

    /**
     * send msg job cron expression
     */
    public static final String SEND_MSG_JOB_CRON_EXPRESSION = "0/5 * * * * ?";

    /**
     * remove invaild ws session job cron expression
     */
    public static final String REMOVE_INVAILD_WS_SESSION_JOB_CRON_EXPRESSION = "0 0/1 * * * ?";

    /**
     * websocket default buffer size
     */
    public static final int WEBSOCKET_DEFAULT_BUFFER_SIZE = 64 * 1024;

    /**
     * websocket max buffer size
     */
    public static final int WEBSOCKET_MAX_BUFFER_SIZE = 250 * 1024;

    /**
     * mq suspend secound
     */
    public static final int MQ_SUSPEND_SECOUND = 5;

    /**
     * mq delay level
     */
    public static final int MQ_DELAY_LEVEL = 2;

    /**
     * service status busy
     */
    public static final String SERVICE_STATUS_BUSY = "BUSY";

    /**
     * service thread pool used count key
     */
    public static final String SERVICE_THREAD_POOL_USED_COUNT_KEY = "threadPoolUsedCount";

    /**
     * agent online num key
     */
    public static final String AGENT_ONLINE_NUM_KEY = "agentOnlineNum";

    /**
     * msg consume count header
     */
    public static final String MSG_CONSUME_COUNT_HEADER = "msgConsumeCount-%d";

    /**
     * msg consume total time header
     */
    public static final String MSG_CONSUME_TOTAL_TIME_HEADER = "msgConsumeTotalTime-%d";

    /**
     * msg consume max time header
     */
    public static final String MSG_CONSUME_MAX_TIME_HEADER = "msgConsumeMaxTime-%d";

    /**
     * msg deal status key
     */
    public static final String MSG_DEAL_STATUS_KEY = "msgDealStatus";

    /**
     * msg deal status key
     */
    public static final String ROCKET_MQ_CONSUME_STATUS_KEY = "rocketmqStatus";

    /**
     * rocket mq consume max diff size
     */
    public static final long ROCKET_MQ_CONSUME_MAX_DIFF_SIZE = 2000;


    private MicroServiceDefine() {
    }
}
