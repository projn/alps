package com.projn.alps.exception.code;

import com.projn.alps.i18n.LocaleContext;
import com.projn.alps.i18n.MessageContext;

import java.io.Serializable;

/**
 * error code
 *
 * @author : sunyuecheng
 */
public interface IErrorCode extends Serializable {
    /**
     * get error key
     *
     * @return String :
     */
    String getErrorKey();

    /**
     * get error code
     *
     * @return String :
     */
    String getErrorCode();

    /**
     * get module name
     *
     * @return String :
     */
    String getModuleName();

    /**
     * get message
     *
     * @param messageParameters :
     * @return String :
     */
    default String getMessage(Object... messageParameters) {
        return MessageContext.getMessage(getModuleName(), LocaleContext.get(), getErrorKey(), messageParameters);
    }
}
