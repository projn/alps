package com.projn.alps.service;

import com.projn.alps.exception.HttpException;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.HttpResponseInfo;

/**
 * components console service
 *
 * @author : sunyuecheng
 */
public interface IComponentsHttpService {

    /**
     * execute
     *
     * @param httpRequestInfo :
     * @return HttpResponseInfo :
     * @throws HttpException :
     */
    HttpResponseInfo execute(HttpRequestInfo httpRequestInfo) throws HttpException;
}
