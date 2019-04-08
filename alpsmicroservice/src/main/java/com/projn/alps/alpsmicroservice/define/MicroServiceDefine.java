package com.projn.alps.alpsmicroservice.define;

/**
 * access server define
 *
 * @author : sunyuecheng
 */
public final class MicroServiceDefine {

    /**
     * default websocket session timeout interval minutes
     */
    public static final int DEFAULT_WEBSOCKET_SESSION_TIMEOUT_INTERVAL_MINUTES = 30;

    /**
     * http api job cron expression
     */
    public static final String HTTP_API_JOB_CRON_EXPRESSION = "0 0/1 * * * ?";

    /**
     * websocket default buffer size
     */
    public static final int WEBSOCKET_DEFAULT_BUFFER_SIZE = 64 * 1024;

    /**
     * websocket max buffer size
     */
    public static final int WEBSOCKET_MAX_BUFFER_SIZE = 250 * 1024;

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
     * msg deal status key
     */
    public static final String MSG_DEAL_STATUS_KEY = "msgDealStatus";


    private MicroServiceDefine() {
    }
}
