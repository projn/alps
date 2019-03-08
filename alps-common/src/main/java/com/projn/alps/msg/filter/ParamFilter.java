package com.projn.alps.msg.filter;

import java.lang.annotation.*;


/**
 * param filter
 *
 * @author : sunyuecheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamFilter {

    /**
     * value
     *
     * @return Class<?> :
     */
    Class<?> value() default Object.class;
}
