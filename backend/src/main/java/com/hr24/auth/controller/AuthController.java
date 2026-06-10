package com.hr24.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;

	@PostMapping("/api/auth/login")
	public String login(@RequestBody LoginRequestDto requestDto) {

		return authService.login(requestDto);
	}

}
