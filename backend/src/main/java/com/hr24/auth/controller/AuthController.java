package com.hr24.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginResponseDto;
import com.hr24.auth.dto.RefreshTokenRequestDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;

	@PostMapping("/login")
	public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {

		return authService.login(requestDto);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<RefreshTokenResponseDto> refresh(
			@RequestBody RefreshTokenRequestDto requestDto
	) {
		RefreshTokenResponseDto responseDto = authService.refresh(requestDto);
		
		return ResponseEntity.ok(responseDto);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
			HttpServletRequest request
	) {
		
		String bearerToken = request.getHeader("Authorization");
		
		String accessToken = bearerToken.substring(7);
		
		authService.logout(accessToken);
		
		return ResponseEntity.ok().build();
	}

}
