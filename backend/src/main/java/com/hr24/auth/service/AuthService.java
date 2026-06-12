package com.hr24.auth.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.RoleRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRoleRepository UserRoleRepository;
	private final RoleRepository roleRepository;
	
	public String login(LoginRequestDto requestDto) {
		
		User user = userRepository.findByLoginId(requestDto.getLoginId())
				.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
		
		boolean passwordMatched = passwordEncoder.matches(
				requestDto.getPassword(),
				user.getPassword()
		);
		
		if (!passwordMatched) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
		
		List<UserRole> useRoles = 
				UserRoleRepository.findByEmployeeId(user.getEmployeeId());
		
		List<Long> roleIds = useRoles.stream()
				.map(UserRole::getRoleId)
				.toList();
		
		List<String> roles = roleRepository.findByRoleIdIn(roleIds)
				.stream()
				.map(Role::getRoleCode)
				.toList(); 
		
		return user.getName() + "님 환영합니다!, 당신의 권한은 " + roles + "입니다.";
	}
}
