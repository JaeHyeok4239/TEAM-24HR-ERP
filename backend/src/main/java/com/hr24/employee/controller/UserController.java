package com.hr24.employee.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.MyInfoResponseDto;
import com.hr24.employee.service.UserService;

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
	
	@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
	@GetMapping("/admin-test")
	public String adminTest() {
	    return "HR_MANAGER success";
	}
	
}
