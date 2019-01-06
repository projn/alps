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
public @interface ParamLimit {

    /**
     * type
     *
     * @return ParamCheckType :
     */
    ParamCheckType type() default ParamCheckType.NULL;

    /**
     * regex
     *
     * @return String :
     */
    String regex() default "";

    /**
     * nullable
     *
     * @return boolean :
     */
    boolean nullable() default true;

    /**
     * value
     *
     * @return String[] :
     */
    String[] value() default {};

    /**
     * max value
     *
     * @return String :
     */
    String maxValue() default "";

    /**
     * min value
     *
     * @return String :
     */
    String minValue() default "";

    /**
     * max length
     *
     * @return int :
     */
    int maxLength() default -1;

    /**
     * min length
     *
     * @return int :
     */
    int minLength() default -1;

    /**
     * precision
     *
     * @return int :
     */
    int precision() default 0;
}
