package com.hr24.employee.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.DepartmentCreateRequestDto;
import com.hr24.employee.dto.DepartmentResponseDto;
import com.hr24.employee.dto.DepartmentUpdateRequestDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

	private static final List<UserStatus> ASSIGNED_EMPLOYEE_STATUSES = List.of(UserStatus.ACTIVE, UserStatus.LOCKED,
			UserStatus.LEAVE);

	private final DepartmentRepository departmentRepository;
	private final UserRepository userRepository;

	// 부서 전체 조회
	public List<DepartmentResponseDto> findAllDepartments() {
		return departmentRepository.findAllWithParentDepartment().stream().map(DepartmentResponseDto::from).toList();
	}

	// 부서 등록
	@Transactional
	public DepartmentResponseDto createDepartment(DepartmentCreateRequestDto requestDto) {
		String departmentCode = requestDto.getDepartmentCode().trim().toUpperCase();

		String departmentName = requestDto.getDepartmentName().trim();

		boolean duplicateCode = departmentRepository.existsByDepartmentCodeIgnoreCase(departmentCode);

		if (duplicateCode) {
			throw new BusinessException(ErrorCode.DUPLICATE_DEPARTMENT_CODE);
		}

		boolean duplicateName = departmentRepository.existsByDepartmentNameIgnoreCase(departmentName);

		if (duplicateName) {
			throw new BusinessException(ErrorCode.DUPLICATE_DEPARTMENT_NAME);
		}

		Department parentDepartment = findAvailableParentDepartment(requestDto.getParentDepartmentId());

		Department department = Department.create(departmentCode, departmentName, parentDepartment,
				requestDto.getActive());

		Department savedDepartment = departmentRepository.save(department);

		return DepartmentResponseDto.from(savedDepartment);
	}

	// 부서수정
	@Transactional
	public DepartmentResponseDto updateDepartment(Long departmentId, DepartmentUpdateRequestDto requestDto) {
		Department department = findDepartment(departmentId);

		Department parentDepartment = findAvailableParentDepartment(requestDto.getParentDepartmentId());

		validateParentDepartment(department, parentDepartment);

		boolean changingToInactive = "Y".equals(department.getIsActive())
				&& Boolean.FALSE.equals(requestDto.getActive());

		if (changingToInactive) {
			validateCanDeactivateDepartment(departmentId);
		}

		String departmentName = requestDto.getDepartmentName().trim();
		boolean duplicateName = departmentRepository.existsByDepartmentNameIgnoreCaseAndDepartmentIdNot(departmentName,
				departmentId);
		if (duplicateName) {
			throw new BusinessException(ErrorCode.DUPLICATE_DEPARTMENT_NAME);
		}

		department.update(departmentName, parentDepartment, requestDto.getActive());

		return DepartmentResponseDto.from(department);
	}

	// 부서ID로 조회
	private Department findDepartment(Long departmentId) {
		return departmentRepository.findById(departmentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));
	}

	// 상위 부서 조회 및 사용 여부 검사
	private Department findAvailableParentDepartment(Long parentDepartmentId) {
		if (parentDepartmentId == null) {
			return null;
		}

		Department parentDepartment = findDepartment(parentDepartmentId);

		if (!"Y".equals(parentDepartment.getIsActive())) {
			throw new BusinessException(ErrorCode.INACTIVE_PARENT_DEPARTMENT);
		}

		return parentDepartment;
	}

	// 자기 자신 또는 자기 하위 부서를 상위 부서로 지정하는 순환 구조 방지
	private void validateParentDepartment(Department department, Department parentDepartment) {
		if (parentDepartment == null) {
			return;
		}

		Set<Long> visitedDepartmentIds = new HashSet<>();

		Department currentDepartment = parentDepartment;

		while (currentDepartment != null) {
			Long currentDepartmentId = currentDepartment.getDepartmentId();

			if (department.getDepartmentId().equals(currentDepartmentId)) {
				throw new BusinessException(ErrorCode.INVALID_PARENT_DEPARTMENT);
			}

			if (!visitedDepartmentIds.add(currentDepartmentId)) {
				throw new BusinessException(ErrorCode.INVALID_PARENT_DEPARTMENT);
			}

			currentDepartment = currentDepartment.getParentDepartment();
		}
	}

	// 부서 미사용 처리 가능 여부 검사
	private void validateCanDeactivateDepartment(Long departmentId) {
		boolean hasAssignedEmployees = userRepository.existsByDepartment_DepartmentIdAndStatusIn(departmentId,
				ASSIGNED_EMPLOYEE_STATUSES);

		if (hasAssignedEmployees) {
			throw new BusinessException(ErrorCode.DEPARTMENT_HAS_ASSIGNED_EMPLOYEES);
		}

		boolean hasActiveChildDepartment = departmentRepository
				.existsByParentDepartment_DepartmentIdAndIsActive(departmentId, "Y");

		if (hasActiveChildDepartment) {
			throw new BusinessException(ErrorCode.DEPARTMENT_HAS_ACTIVE_CHILDREN);
		}
	}
}