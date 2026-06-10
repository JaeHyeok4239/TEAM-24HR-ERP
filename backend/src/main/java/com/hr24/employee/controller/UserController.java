package com.hr24.employee.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepository;
	
	@GetMapping("/api/users/test")
	public User testUser() {
		return userRepository.findByLoginId("ceo")
				             .orElseThrow();
	}

}
