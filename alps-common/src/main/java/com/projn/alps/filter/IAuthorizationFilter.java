package com.projn.alps.filter;

import java.util.List;

/**
 * authorization filter
 *
 * @author : sunyuecheng
 */
public interface IAuthorizationFilter {

    /**
     * auth
     *
     * @param authObj          :
     * @param userRoleNameList :
     * @return boolean :
     * @throws Exception :
     */
    boolean auth(Object authObj, List<String> userRoleNameList) throws Exception;
}
