package com.hr24.auth.cookie;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7);
    private static final String COOKIE_PATH = "/";

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {

        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(COOKIE_PATH)
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite(cookieSameSite)
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie() {

        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(COOKIE_PATH)
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();
    }
}