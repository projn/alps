package com.projn.alps.alpsmicroservice.handler;

import com.alibaba.fastjson.JSON;
import com.projn.alps.define.HttpDefine;
import com.projn.alps.exception.HttpException;
import com.projn.alps.msg.response.HttpErrorResponseMsgInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http exception handler
 *
 * @author : sunyuecheng
 */
public class HttpExceptionHandler implements HandlerExceptionResolver {

    /**
     * resolve exception
     *
     * @param request  :
     * @param response :
     * @param handler  :
     * @param ex       :
     * @return org.springframework.web.servlet.ModelAndView :
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        response.setContentType(HttpDefine.CONTENT_TYPE_APPLICATION_JSON_UTF_8);

        if (HttpException.class.isInstance(ex)) {
            HttpException httpException = (HttpException) ex;

            response.setStatus(httpException.getHttpStatus());

            HttpErrorResponseMsgInfo httpErrorResponseMsgInfo =
                    new HttpErrorResponseMsgInfo(httpException.getErrorCode(), httpException.getErrorDescription());

            try {
                response.getWriter().print(JSON.toJSONString(httpErrorResponseMsgInfo));
            } catch (IOException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return new ModelAndView();
    }
}
