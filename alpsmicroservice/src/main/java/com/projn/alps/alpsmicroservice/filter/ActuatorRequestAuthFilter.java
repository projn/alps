package com.projn.alps.alpsmicroservice.filter;

import com.alibaba.fastjson.JSONObject;
import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.filter.IAuthorizationFilter;
import com.projn.alps.msg.response.HttpErrorResponseMsgInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static com.projn.alps.define.HttpDefine.CONTENT_TYPE_APPLICATION_JSON_UTF_8;
import static com.projn.alps.exception.code.CommonErrorCode.RESULT_INVAILD_USER_TOKEN_ERROR;

/**
 * actuator request auth filter
 *
 * @author : sunyuecheng
 */
@Component
@ConditionalOnProperty(name = "system.bean.switch.actuator.auth", havingValue = "true", matchIfMissing = true)
public class ActuatorRequestAuthFilter extends OncePerRequestFilter {

    @Autowired
    private RunTimeProperties runTimeProperties;


    @Autowired
    @Qualifier("JwtAuthenticationFilter")
    private IAuthorizationFilter authorizationFilter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        try {

            List<String> userRoleNameList
                    = Arrays.asList(runTimeProperties.getApiAccessRoleActuator().split(","));
            authorizationFilter.auth(request, userRoleNameList);

        } catch (Exception e) {
            HttpErrorResponseMsgInfo httpErrorResponseMsgInfo = new HttpErrorResponseMsgInfo();
            httpErrorResponseMsgInfo.setErrorCode(RESULT_INVAILD_USER_TOKEN_ERROR.getErrorCode());
            httpErrorResponseMsgInfo.setErrorDescription(e.getMessage());

            String responseStr = JSONObject.toJSONString(httpErrorResponseMsgInfo);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE_APPLICATION_JSON_UTF_8);

            PrintWriter printWriter = null;
            try {
                printWriter = response.getWriter();
                printWriter.append(responseStr);
            } catch (IOException eq) {
                eq.printStackTrace();
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
            return;
        }

        chain.doFilter(request, response);
    }
}

