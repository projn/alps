package com.projn.alps.define;

/**
 * common define
 *
 * @author : sunyuecheng
 */
public final class CommonDefine {
    /**
     * max agent msg id
     */
    public static final int MAX_AGENT_MSG_ID = 1009;

    /**
     * min agent msg id
     */
    public static final int MIN_AGENT_MSG_ID = 1001;

    /**
     * default encoding
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * connect server master url format
     */
    public static final String CONNECT_SERVER_MASTER_URL_FORMAT = "http://%s/hostmgr/http";

    /**
     * max batch sql item size
     */
    public static final long MAX_BATCH_SQL_ITEM_SIZE = 1000;

    /**
     * collection init size
     */
    public static final int COLLECTION_INIT_SIZE = 16;

    /**
     * min ip address len
     */
    public static final int MIN_IP_ADDRESS_LEN = 7;

    /**
     * max ip address len
     */
    public static final int MAX_IP_ADDRESS_LEN = 15;

    /**
     * radix 10
     */
    public static final int RADIX_10 = 10;

    /**
     * milli second 1000
     */
    public static final long MILLI_SECOND_1000 = 1000L;

    /**
     * one year second
     */
    public static final long ONE_YEAR_SECOND = 365 * 24 * 3600L;

    /**
     * one week second
     */
    public static final long ONE_WEEK_SECOND = 7 * 24 * 3600L;

    /**
     * one day second
     */
    public static final long ONE_DAY_SECOND = 24 * 3600L;

    /**
     * one hour second
     */
    public static final long ONE_HOUR_SECOND = 3600L;

    /**
     * one minute second
     */
    public static final long ONE_MINUTE_SECOND = 60L;

    /**
     * day hours
     */
    public static final int DAY_HOURS = 24;

    /**
     * day hours
     */
    public static final double PERCENT_100 = 100.00;

    /**
     * bit len
     */
    public static final int BIT_LEN = 8;

    /**
     * star division flag
     */
    public static final String STAR_DIVISION_FLAG = "*";

    /**
     * point division flag
     */
    public static final String POINT_DIVISION_FLAG = ".";

    /**
     * lang en us
     */
    public static final String LANG_EN_US = "en-us";

    /**
     * max http response wait seconds
     */
    public static final int MAX_HTTP_RESPONSE_WAIT_SECONDS = 30;

    /**
     * msg response max time header
     */
    public static final String MSG_RESPONSE_MAX_TIME_HEADER = "msgResponseMaxTime-%s";

    /**
     * read buffer size
     */
    public static final int READ_BUFFER_SIZE = 1024 * 32;


    private CommonDefine() {
    }
}
