package com.projn.alps.msg.filter;

/**
 * param filter
 *
 * @author : sunyuecheng
 */
public interface IParamFilter {

    /**
     * check param
     *
     * @param param :
     * @return boolean :
     * @throws Exception :
     */
    boolean checkParam(Object param) throws Exception;
}
