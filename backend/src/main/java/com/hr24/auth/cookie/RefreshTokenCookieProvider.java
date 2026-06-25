package com.hr24.auth.cookie;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7);
    private static final String COOKIE_PATH = "/";

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {

        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false) // localhost 개발 환경
                .path(COOKIE_PATH)
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {

        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false) // localhost 개발 환경
                .path(COOKIE_PATH)
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
    
    //나중에 수정 해야함
}