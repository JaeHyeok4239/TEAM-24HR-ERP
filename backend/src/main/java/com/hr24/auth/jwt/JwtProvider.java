package com.hr24.auth.jwt;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;




@Component
public class JwtProvider {
	
	private static final String CLAIM_EMPLOYEE_ID = "employeeId";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";
	
	@Value("${jwt.secret}")
	private String secretKey;
	
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;
	
	private SecretKey getSigningKey() {
	    return Keys.hmacShaKeyFor(
	            secretKey.getBytes(StandardCharsets.UTF_8)
	    );
	}
	
	private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
	
	public String createAccessToken(
            Long employeeId,
            String loginId,
            List<String> roles
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(loginId)
                .claim(CLAIM_EMPLOYEE_ID, employeeId)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }
	
	public String createRefreshToken(Long employeeId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .claim(CLAIM_EMPLOYEE_ID, employeeId)
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }
	
	public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginId(String token) {
        return getClaims(token).getSubject();
    }

    public Long getEmployeeId(String token) {
        return getClaims(token).get(CLAIM_EMPLOYEE_ID, Long.class);
    }

    public String getTokenType(String token) {
        return getClaims(token).get(CLAIM_TOKEN_TYPE, String.class);
    }
}
