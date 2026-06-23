package com.hr24.employee.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.EmployeeHistory;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.enums.EmployeeHistoryItem;
import com.hr24.employee.repository.EmployeeHistoryRepository;
import com.hr24.employee.repository.RoleRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeRoleService {

	private static final String DEFAULT_ROLE_CODE = "USER";
	private static final String ACTIVE_YN = "Y";

	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final EmployeeHistoryRepository employeeHistoryRepository;

	// 직원 권한 코드 목록 조회
	public List<String> findRoleCodes(Long employeeId) {
		return userRoleRepository.findAllWithRoleByEmployeeId(employeeId)
				.stream()
				.map(userRole -> userRole.getRole().getRoleCode())
				.toList();
	}

	// 직원 등록 시 기본 USER 권한 부여
	@Transactional
	public void assignDefaultUserRole(User user) {
		Role role = roleRepository
				.findByRoleCodeAndIsActive(DEFAULT_ROLE_CODE, ACTIVE_YN)
				.orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

		UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setRole(role);

		userRoleRepository.save(userRole);
	}

	// 직원 접근 권한 수정
	@Transactional
	public List<String> updateEmployeeRoles(
			User employee,
			User changedBy,
			List<String> requestedRoleCodes,
			String changeReason
	) {
		List<UserRole> currentUserRoles =
				userRoleRepository.findAllWithRoleByEmployeeId(employee.getEmployeeId());

		List<String> currentRoleCodes = currentUserRoles.stream()
				.map(userRole -> userRole.getRole().getRoleCode())
				.toList();

		List<String> normalizedRoleCodes = normalizeRoleCodes(requestedRoleCodes);

		if (sameRoleCodes(currentRoleCodes, normalizedRoleCodes)) {
			throw new BusinessException(ErrorCode.NO_EMPLOYEE_ROLE_CHANGED);
		}

		List<Role> roles = roleRepository.findAllByRoleCodeInAndIsActive(
				normalizedRoleCodes,
				ACTIVE_YN
		);

		if (roles.size() != normalizedRoleCodes.size()) {
			throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
		}

		deleteRemovedRoles(currentUserRoles, normalizedRoleCodes);
		addNewRoles(employee, currentRoleCodes, normalizedRoleCodes, roles);

		saveRoleHistory(
				employee,
				changedBy,
				currentRoleCodes,
				normalizedRoleCodes,
				changeReason
		);

		return normalizedRoleCodes;
	}

	// 요청 권한 코드 정리
	private List<String> normalizeRoleCodes(List<String> requestedRoleCodes) {
		Set<String> roleCodeSet = new LinkedHashSet<>();

		roleCodeSet.add(DEFAULT_ROLE_CODE);

		if (requestedRoleCodes != null) {
			for (String roleCode : requestedRoleCodes) {
				if (roleCode == null || roleCode.isBlank()) {
					continue;
				}

				roleCodeSet.add(roleCode.trim().toUpperCase());
			}
		}

		return new ArrayList<>(roleCodeSet);
	}

	// 현재 권한과 요청 권한이 같은지 확인
	private boolean sameRoleCodes(
			List<String> currentRoleCodes,
			List<String> requestedRoleCodes
	) {
		return Set.copyOf(currentRoleCodes).equals(Set.copyOf(requestedRoleCodes));
	}

	// 제거된 권한 삭제
	private void deleteRemovedRoles(
			List<UserRole> currentUserRoles,
			List<String> requestedRoleCodes
	) {
		List<UserRole> rolesToDelete = currentUserRoles.stream()
				.filter(userRole ->
						!requestedRoleCodes.contains(userRole.getRole().getRoleCode())
				)
				.toList();

		userRoleRepository.deleteAll(rolesToDelete);
	}

	// 새로 추가된 권한 저장
	private void addNewRoles(
			User employee,
			List<String> currentRoleCodes,
			List<String> requestedRoleCodes,
			List<Role> roles
	) {
		List<UserRole> rolesToSave = roles.stream()
				.filter(role -> !currentRoleCodes.contains(role.getRoleCode()))
				.map(role -> {
					UserRole userRole = new UserRole();
					userRole.setUser(employee);
					userRole.setRole(role);
					return userRole;
				})
				.toList();

		userRoleRepository.saveAll(rolesToSave);
	}

	// 권한 변경 이력 저장
	private void saveRoleHistory(
			User employee,
			User changedBy,
			List<String> beforeRoleCodes,
			List<String> afterRoleCodes,
			String changeReason
	) {
		EmployeeHistory history = EmployeeHistory.create(
				employee,
				changedBy,
				EmployeeHistoryItem.ROLE,
				String.join(", ", beforeRoleCodes),
				String.join(", ", afterRoleCodes),
				changeReason
		);

		employeeHistoryRepository.save(history);
	}
}