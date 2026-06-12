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

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;
	
	private Key getSigningKey() {
	    return Keys.hmacShaKeyFor(
	            secretKey.getBytes(StandardCharsets.UTF_8)
	    );
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
	            .claim("employeeId", employeeId)
	            .claim("roles", roles)
	            .claim("tokenType", "ACCESS")
	            .issuedAt(now)
	            .expiration(expiration)
	            .signWith(getSigningKey())
	            .compact();
	}
	
	public String createRefreshToken(Long employeeId) {
	    Date now = new Date();
	    Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

	    return Jwts.builder()
	            .claim("employeeId", employeeId)
	            .claim("tokenType", "REFRESH")
	            .issuedAt(now)
	            .expiration(expiryDate)
	            .signWith(getSigningKey())
	            .compact();
	}
	
	public boolean validateToken(String token) {
		
		try {
			Key key = Keys.hmacShaKeyFor(
					secretKey.getBytes(StandardCharsets.UTF_8)
			);
			
			Jwts.parser()
					.verifyWith((javax.crypto.SecretKey) key)
					.build()
					.parseSignedClaims(token);
			
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getLoginId(String token) {
		Key key = Keys.hmacShaKeyFor(
				secretKey.getBytes(StandardCharsets.UTF_8)
		);
		
		return Jwts.parser()
				.verifyWith((javax.crypto.SecretKey) key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public Long getEmployeeId(String token) {
		
		Key key = Keys.hmacShaKeyFor(
				secretKey.getBytes(StandardCharsets.UTF_8)
		);
		
		return Jwts.parser()
				.verifyWith((javax.crypto.SecretKey) key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("employeeId", Long.class);
	}
	
	public String getTokenType(String token) {

	    Key key = Keys.hmacShaKeyFor(
	            secretKey.getBytes(StandardCharsets.UTF_8)
	    );

	    return Jwts.parser()
	            .verifyWith((javax.crypto.SecretKey) key)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .get("tokenType", String.class);
	}
}
