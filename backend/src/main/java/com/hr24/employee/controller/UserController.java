package com.hr24.employee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.user.MyInfoResponseDto;
import com.hr24.employee.dto.user.MyInfoUpdateRequestDto;
import com.hr24.employee.dto.user.PasswordChangeRequestDto;
import com.hr24.employee.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "내 정보 관리", description = "로그인 사용자의 내 정보 조회, 수정, 비밀번호 변경 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	
	private final UserService userService;
	
	@Operation(
	        summary = "내 정보 조회",
	        description = "현재 로그인한 사용자의 기본 정보를 조회합니다."
	)
	@GetMapping("/me")
	public MyInfoResponseDto getMyInfo() {
		return userService.getMyInfo();
	}
	
	@Operation(
	        summary = "내 정보 수정",
	        description = "현재 로그인한 사용자의 연락처, 이메일, 주소 등 개인 정보를 수정합니다."
	)
	@PatchMapping("/me")
	public MyInfoResponseDto updateMyInfo(
			@Valid @RequestBody MyInfoUpdateRequestDto requestDto
	) {
		return userService.updateMyInfo(requestDto);
	}
	
	@Operation(
	        summary = "내 비밀번호 변경",
	        description = "현재 로그인한 사용자의 비밀번호를 변경합니다."
	)
	@PatchMapping("/me/password")
	public ResponseEntity<Void> changePassword(
			@Valid @RequestBody PasswordChangeRequestDto requestDto
	) {
		userService.changePassword(requestDto);
		
		return ResponseEntity.noContent().build();
	}
}
