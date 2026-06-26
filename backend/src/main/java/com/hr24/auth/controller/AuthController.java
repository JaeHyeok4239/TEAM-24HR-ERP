package com.hr24.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.auth.cookie.RefreshTokenCookieProvider;
import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginResponseDto;
import com.hr24.auth.dto.LoginTokenDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 관리", description = "로그인, 로그아웃, 토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;
	private final RefreshTokenCookieProvider refreshTokenCookieProvider;

	@Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
		@ApiResponse(responseCode = "404", description = "사용자 없음")
	})
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(
	        @RequestBody LoginRequestDto requestDto
	) {

	    LoginTokenDto tokenDto =
	            authService.login(requestDto);

	    ResponseCookie refreshTokenCookie =
                refreshTokenCookieProvider.createRefreshTokenCookie(
                        tokenDto.refreshToken()
                );

        LoginResponseDto responseDto =
                new LoginResponseDto(tokenDto.accessToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(responseDto);
    }
	
	@Operation(summary = "토큰 재발급", description = "HttpOnly Cookie의 Refresh Token으로 Access Token을 재발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refresh(
            @CookieValue(
                    name = RefreshTokenCookieProvider.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            )
            String refreshToken
    ) {

        RefreshTokenResponseDto responseDto =
                authService.refresh(refreshToken);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "로그아웃", description = "Redis의 Refresh Token을 삭제하고 Refresh Token Cookie를 만료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @CookieValue(
                    name = RefreshTokenCookieProvider.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            )
            String refreshToken
    ) {

        String accessToken =
                resolveAccessToken(request);

        authService.logout(
                accessToken,
                refreshToken
        );

        ResponseCookie expiredCookie =
                refreshTokenCookieProvider.deleteRefreshTokenCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }

    private String resolveAccessToken(HttpServletRequest request) {

        String bearerToken =
                request.getHeader("Authorization");

        if (
                bearerToken == null
                || !bearerToken.startsWith("Bearer ")
        ) {
            return null;
        }

        return bearerToken.substring(7);
    }
}