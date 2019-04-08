package com.projn.alps.filter.impl;

import com.projn.alps.filter.IAuthorizationFilter;
import com.projn.alps.initialize.ServiceData;
import com.projn.alps.util.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.projn.alps.define.HttpDefine.HEADER_JWT_TOKEN;
import static com.projn.alps.define.HttpDefine.JWT_TOKEN_PREFIX;

/**
 * jwt authentication filter impl
 *
 * @author : sunyuecheng
 */
@Component("HttpJwtAuthFilter")
public class HttpJwtAuthFilterImpl implements IAuthorizationFilter {

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
        String authHeader = request.getHeader(HEADER_JWT_TOKEN);
        if (authHeader != null && authHeader.startsWith(JWT_TOKEN_PREFIX)) {
            final String authToken = authHeader.substring(JWT_TOKEN_PREFIX.length());

            Claims claims = JwtTokenUtils.parseToken(authToken, ServiceData.getJwtSecretKey());
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

