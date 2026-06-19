package com.hr24.employee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.user.MyInfoResponseDto;
import com.hr24.employee.dto.user.MyInfoUpdateRequestDto;
import com.hr24.employee.dto.user.PasswordChangeRequestDto;
import com.hr24.employee.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/me")
	public MyInfoResponseDto getMyInfo() {
		return userService.getMyInfo();
	}
	
	@PatchMapping("/me")
	public MyInfoResponseDto updateMyInfo(
			@Valid @RequestBody MyInfoUpdateRequestDto requestDto
	) {
		return userService.updateMyInfo(requestDto);
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<Void> changePassword(
			@Valid @RequestBody PasswordChangeRequestDto requestDto
	) {
		userService.changePassword(requestDto);
		
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
	@GetMapping("/admin-test")
	public String adminTest() {
	    return "HR_MANAGER success";
	}
	
}
