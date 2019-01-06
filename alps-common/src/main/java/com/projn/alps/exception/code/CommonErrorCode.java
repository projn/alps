package com.projn.alps.exception.code;

/**
 * common error code
 *
 * @author : sunyuecheng
 */
public enum CommonErrorCode implements IErrorCode {
    /**
     * success
     */
    RESULT_OK("00000000"),

    /**
     * invaild param error
     */
    RESULT_INVAILD_PARAM_ERROR("00000001"),

    /**
     * analyse request error
     */
    RESULT_ANALYSE_REQUEST_ERROR("00000002"),

    /**
     * system is busy error
     */
    RESULT_SYSTEM_IS_BUSY_ERROR("00000003"),

    /**
     * query db error
     */
    RESULT_SQL_ERROR("00000004"),

    /**
     * query redis error
     */
    RESULT_CACHE_ERROR("00000005"),

    /**
     * file error
     */
    RESULT_FILE_ERROR("00000006"),

    /**
     * thread task error
     */
    RESULT_TASK_ERROR("00000007"),

    /**
     * system inter error
     */
    RESULT_SYSTEM_INTER_ERROR("00000008"),

    /**
     * third interface error
     */
    RESULT_THIRD_INTERFACE_ERROR("00000009"),

    /**
     * auth error
     */
    RESULT_ACCESS_ERROR("00000010"),

    /**
     * reuqest info format error
     */
    RESULT_INVALID_REQUEST_INFO_ERROR("00000011"),

    /**
     * invaild user token error
     */
    RESULT_INVAILD_USER_TOKEN_ERROR("00000012");

    private static final String MODULE_NAME = "common";

    private String errorCode;

    CommonErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorKey() {
        return name();
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }
}
