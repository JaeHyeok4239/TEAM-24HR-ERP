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
import com.hr24.auth.dto.PasswordResetCodeSendRequestDto;
import com.hr24.auth.dto.PasswordResetCodeVerifyRequestDto;
import com.hr24.auth.dto.PasswordResetCodeVerifyResponseDto;
import com.hr24.auth.dto.PasswordResetRequestDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 관리", description = "로그인, 로그아웃, 토큰 재발급, 비밀번호 재설정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;
	private final RefreshTokenCookieProvider refreshTokenCookieProvider;

	@Operation(
	        summary = "로그인",
	        description = "로그인 성공 시 Access Token을 응답 body로 반환하고, Refresh Token은 HttpOnly Cookie로 저장합니다."
	)
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(
	       @Valid @RequestBody LoginRequestDto requestDto
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
	
	@Operation(
	        summary = "Access Token 재발급",
	        description = "HttpOnly Cookie에 저장된 Refresh Token을 검증하고 새로운 Access Token을 발급합니다."
	)
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

	@Operation(
	        summary = "로그아웃",
	        description = "Redis에 저장된 Refresh Token을 삭제하고 Refresh Token Cookie를 만료합니다."
	)
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
    
	@Operation(
	        summary = "비밀번호 재설정 인증코드 발송",
	        description = "로그인 ID와 이메일이 일치하면 비밀번호 재설정 인증코드를 이메일로 발송합니다."
	)
    @PostMapping("/password-reset/code")
    public ResponseEntity<Void> sendPasswordResetCode(
            @Valid @RequestBody PasswordResetCodeSendRequestDto requestDto
    ) {
        authService.sendPasswordResetCode(requestDto);

        return ResponseEntity.noContent().build();
    }
    
	@Operation(
	        summary = "비밀번호 재설정 인증코드 확인",
	        description = "이메일로 발송된 인증코드를 검증하고 비밀번호 재설정용 resetToken을 발급합니다."
	)
    @PostMapping("/password-reset/code/verify")
    public ResponseEntity<PasswordResetCodeVerifyResponseDto> verifyPasswordResetCode(
            @Valid @RequestBody PasswordResetCodeVerifyRequestDto requestDto
    ) {
        PasswordResetCodeVerifyResponseDto responseDto =
                authService.verifyPasswordResetCode(requestDto);

        return ResponseEntity.ok(responseDto);
    }
    
	@Operation(
	        summary = "비밀번호 재설정",
	        description = "resetToken을 검증한 뒤 새 비밀번호로 변경합니다."
	)
    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody PasswordResetRequestDto requestDto
    ) {
        authService.resetPassword(requestDto);

        return ResponseEntity.noContent().build();
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