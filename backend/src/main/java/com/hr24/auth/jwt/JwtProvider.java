package com.hr24.auth.jwt;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;




@Component
public class JwtProvider {
	
	@Value("${jwt.secret}")
	private String secretKey;
	
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;
	
	public String createToken(
	        Long employeeId,
	        String loginId,
	        List<String> roles
	) {
	    Date now = new Date();
	    Date expiration = new Date(now.getTime() + accessTokenExpiration);

	    Key key = Keys.hmacShaKeyFor(
	            secretKey.getBytes(StandardCharsets.UTF_8)
	    );

	    return Jwts.builder()
	            .subject(loginId)
	            .claim("employeeId", employeeId)
	            .claim("roles", roles)
	            .issuedAt(now)
	            .expiration(expiration)
	            .signWith(key)
	            .compact();
	}
}
