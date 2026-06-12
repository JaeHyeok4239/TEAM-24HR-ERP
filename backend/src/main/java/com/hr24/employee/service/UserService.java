package com.hr24.employee.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hr24.employee.dto.MyInfoResponseDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.PositionRepository;
import com.hr24.employee.repository.RoleRepository;
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
	private final RoleRepository roleRepository;
	private final DepartmentRepository departmentRepository;
	private final PositionRepository positionRepository;
	
	public MyInfoResponseDto getMyInfo() {
		
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		
		String loginId = authentication.getName();
		
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		Department department = departmentRepository.findById(user.getDepartmentId())
		        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Position position = positionRepository.findById(user.getPositionId())
		        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		List<UserRole> userRoles =
				userRoleRepository.findByEmployeeId(user.getEmployeeId());
		
		List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .toList();

        List<String> roles = roleRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(Role::getRoleCode)
                .toList();

        return new MyInfoResponseDto(
                user.getEmployeeId(),
                user.getEmployeeNo(),
                user.getLoginId(),
                user.getName(),
                department.getDepartmentName(),
                position.getPositionName(),
                roles
        );
	}
}
