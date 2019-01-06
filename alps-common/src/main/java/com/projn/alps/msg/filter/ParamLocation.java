package com.projn.alps.msg.filter;

import java.lang.annotation.*;

/**
 * param limit
 *
 * @author : sunyuecheng
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamLocation {

    /**
     * location
     *
     * @return String :
     */
    ParamLocationType location() default ParamLocationType.PATH;
}
