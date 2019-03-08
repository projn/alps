package com.projn.alps.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * jwt token utils
 *
 * @author : sunyuecheng
 */
public final class JwtTokenUtils {
    /**
     * create token
     *
     * @param audience  :
     * @param claimMap  :
     * @param subject   :
     * @param issuer    :
     * @param ttl       :
     * @param secretKey :
     * @return String :
     */
    public static String createToken(String audience, Map<String, Object> claimMap,
                                     String subject, String issuer,
                                     long ttl, String secretKey) {
        if (StringUtils.isEmpty(audience) || StringUtils.isEmpty(secretKey)) {
            return null;
        }

        Date createDate = new Date();
        Date expiresDate = createDate;

        if (ttl >= 0) {
            expiresDate = new Date(createDate.getTime() + ttl);
        }

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setAudience(audience)
                .setClaims(claimMap)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(createDate)
                .setExpiration(expiresDate)
                .signWith(signatureAlgorithm, secretKey);
        return builder.compact();
    }

    /**
     * parse token
     *
     * @param jwtToken  :
     * @param secretKey :
     * @return Claims :
     */
    public static Claims parseToken(String jwtToken, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private JwtTokenUtils() {
    }
}
