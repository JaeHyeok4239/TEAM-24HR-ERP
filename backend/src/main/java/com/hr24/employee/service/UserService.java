package com.hr24.employee.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.MyInfoResponseDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	@Transactional(readOnly = true)
	public MyInfoResponseDto getMyInfo() {
		
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		
		String loginId = authentication.getName();
		
		User user = userRepository.findByLoginIdWithDepartmentAndPosition(loginId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		Department department = user.getDepartment();
		Position position = user.getPosition();
		   
		List<UserRole> userRoles =
				userRoleRepository.findAllWithRoleByEmployeeId(user.getEmployeeId());
		
		List<String> roles = userRoles.stream()
		        .map(userRole ->
		                userRole.getRole().getRoleCode()
		        )
		        .toList();

		return new MyInfoResponseDto(
		        user.getEmployeeNo(),
		        user.getLoginId(),
		        user.getName(),

		        department != null
		                ? department.getDepartmentName()
		                : null,

		        position != null
		                ? position.getPositionName()
		                : null,

		        user.getHireDate() != null
		                ? user.getHireDate().toLocalDate()
		                : null,

		        user.getEmail(),
		        user.getPhone(),
		        user.getZipcode(),
		        user.getAddress(),
		        user.getAddressDetail(),

		        roles
		);
	}
}
