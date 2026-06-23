package com.hr24.employee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.hr.DepartmentTreeResponseDto;
import com.hr24.employee.dto.hr.DepartmentTreeResponseDto.DepartmentTreeNodeDto;
import com.hr24.employee.dto.hr.EmployeeDetailResponseDto;
import com.hr24.employee.dto.hr.EmployeeFormOptionsResponseDto;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.PositionRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrEmployeeQueryService {

	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;
	private final PositionRepository positionRepository;
	private final EmployeeRoleService employeeRoleService;
	private final EmployeeSensitiveInfoService employeeSensitiveInfoService;

	public List<EmployeeListResponseDto> findEmployees(
			Long departmentId,
			UserStatus status,
			EmploymentType employmentType,
			String keyword
	) {
		String normalizedKeyword = normalizeKeyword(keyword);

		return userRepository.searchHrEmployees(
						departmentId,
						status,
						employmentType,
						normalizedKeyword
				)
				.stream()
				.map(EmployeeListResponseDto::from)
				.toList();
	}

	public EmployeeDetailResponseDto findEmployeeDetail(Long employeeId) {
		User user = userRepository.findByEmployeeIdWithDepartmentAndPosition(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		List<String> roles = employeeRoleService.findRoleCodes(user.getEmployeeId());

		return toEmployeeDetailResponse(user, roles);
	}

	public DepartmentTreeResponseDto findDepartmentTree() {
		long totalRegularActiveEmployeeCount = userRepository.countByStatusAndEmploymentType(
				UserStatus.ACTIVE,
				EmploymentType.REGULAR
		);

		long totalDailyActiveEmployeeCount = userRepository.countByStatusAndEmploymentType(
				UserStatus.ACTIVE,
				EmploymentType.DAILY
		);

		List<Department> departments = departmentRepository.findAllActiveWithParentDepartment();

		Map<Long, DepartmentTreeNodeDto> nodeMap = new HashMap<>();
		Map<Long, List<DepartmentTreeNodeDto>> childrenMap = new HashMap<>();

		for (Department department : departments) {
			long regularActiveEmployeeCount =
					userRepository.countByDepartment_DepartmentIdAndStatusAndEmploymentType(
							department.getDepartmentId(),
							UserStatus.ACTIVE,
							EmploymentType.REGULAR
					);

			DepartmentTreeNodeDto node = DepartmentTreeNodeDto.of(
					department,
					regularActiveEmployeeCount,
					new ArrayList<>()
			);

			nodeMap.put(department.getDepartmentId(), node);
			childrenMap.put(department.getDepartmentId(), node.getChildren());
		}

		List<DepartmentTreeNodeDto> rootNodes = new ArrayList<>();

		for (Department department : departments) {
			DepartmentTreeNodeDto currentNode = nodeMap.get(department.getDepartmentId());
			Department parentDepartment = department.getParentDepartment();

			if (parentDepartment == null || !nodeMap.containsKey(parentDepartment.getDepartmentId())) {
				rootNodes.add(currentNode);
				continue;
			}

			List<DepartmentTreeNodeDto> parentChildren =
					childrenMap.get(parentDepartment.getDepartmentId());

			parentChildren.add(currentNode);
		}

		return new DepartmentTreeResponseDto(
				totalRegularActiveEmployeeCount,
				totalDailyActiveEmployeeCount,
				rootNodes
		);
	}

	public EmployeeFormOptionsResponseDto findEmployeeFormOptions() {
		List<EmployeeFormOptionsResponseDto.DepartmentOptionDto> departments =
				departmentRepository.findAllActiveWithParentDepartment()
						.stream()
						.map(EmployeeFormOptionsResponseDto.DepartmentOptionDto::from)
						.toList();

		List<EmployeeFormOptionsResponseDto.PositionOptionDto> positions =
				positionRepository.findAll()
						.stream()
						.filter(position -> !"N".equals(position.getIsActive()))
						.map(EmployeeFormOptionsResponseDto.PositionOptionDto::from)
						.toList();

		return new EmployeeFormOptionsResponseDto(
				departments,
				positions
		);
	}

	private EmployeeDetailResponseDto toEmployeeDetailResponse(
			User user,
			List<String> roles
	) {
		return EmployeeDetailResponseDto.from(
				user,
				roles,
				employeeSensitiveInfoService.getMaskedAccountNumber(user),
				employeeSensitiveInfoService.getMaskedRrn(user)
		);
	}

	private String normalizeKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		return keyword.trim();
	}
}