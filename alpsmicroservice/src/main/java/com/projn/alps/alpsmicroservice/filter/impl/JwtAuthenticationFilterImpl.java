package com.projn.alps.alpsmicroservice.filter.impl;

import com.projn.alps.alpsmicroservice.property.RunTimeProperties;
import com.projn.alps.filter.IAuthorizationFilter;
import com.projn.alps.util.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * jwt authentication filter impl
 *
 * @author : sunyuecheng
 */
@Component("JwtAuthenticationFilter")
public class JwtAuthenticationFilterImpl implements IAuthorizationFilter {

    /**
     * token prefix
     */
    public static final String TOKEN_PREFIX = "token";

    /**
     * token header string
     */
    public static final String TOKEN_HEADER_STRING = "Authorization";

    @Autowired
    private RunTimeProperties runTimeProperties;

    /**
     * auth
     *
     * @param authObj          :
     * @param userRoleNameList :
     * @return boolean :
     * @throws Exception :
     */
    @Override
    public boolean auth(Object authObj, List<String> userRoleNameList) throws Exception {
        if (authObj == null || userRoleNameList == null) {
            throw new Exception("Invaild param.");
        }

        HttpServletRequest request = (HttpServletRequest) authObj;
        String authHeader = request.getHeader(TOKEN_HEADER_STRING);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            final String authToken = authHeader.substring(TOKEN_PREFIX.length());

            Claims claims = JwtTokenUtils.parseToken(authToken, runTimeProperties.getTokenSecretKey());
            String role = (String) claims.get("ROLE");
            if (!StringUtils.isEmpty(role) && !userRoleNameList.contains(role)) {
                throw new Exception("This role has no access rights.");
            }
        } else {
            throw new Exception("Invaild requset info.");
        }
        return true;
    }
}

