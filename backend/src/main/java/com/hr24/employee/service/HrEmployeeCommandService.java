package com.hr24.employee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.hr.EmployeeBasicInfoUpdateRequestDto;
import com.hr24.employee.dto.hr.EmployeeCreateRequestDto;
import com.hr24.employee.dto.hr.EmployeeDetailResponseDto;
import com.hr24.employee.dto.hr.EmployeeEmploymentInfoUpdateRequestDto;
import com.hr24.employee.dto.hr.EmployeeRoleUpdateRequestDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
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
@Transactional
public class HrEmployeeCommandService {

	private static final String EMPLOYEE_NO_PREFIX = "EMP";
	private static final int MAX_EMPLOYEE_NO_SEQUENCE = 9999;

	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;
	private final PositionRepository positionRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmployeeHistoryService employeeHistoryService;
	private final EmployeeRoleService employeeRoleService;
	private final EmployeeSensitiveInfoService employeeSensitiveInfoService;

	public EmployeeDetailResponseDto createEmployee(EmployeeCreateRequestDto request) {
		validateDuplicateLoginId(request.getLoginId());
		validateDuplicateEmail(request.getEmail());

		Department department = null;
		Position position = null;

		if (request.getEmploymentType() == EmploymentType.REGULAR) {
			validateRegularEmployeeRequiredFields(request);

			department = departmentRepository.findById(request.getDepartmentId())
					.orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

			position = positionRepository.findById(request.getPositionId())
					.orElseThrow(() -> new BusinessException(ErrorCode.POSITION_NOT_FOUND));
		}

		String employeeNo = generateEmployeeNo();
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.createEmployee(
				employeeNo,
				request.getLoginId().trim(),
				encodedPassword,
				request.getName().trim(),
				request.getEmail().trim(),
				department,
				position,
				request.getEmploymentType()
		);

		User savedUser = userRepository.save(user);

		employeeRoleService.assignDefaultUserRole(savedUser);

		List<String> roles = employeeRoleService.findRoleCodes(savedUser.getEmployeeId());

		return toEmployeeDetailResponse(savedUser, roles);
	}

	public EmployeeDetailResponseDto updateBasicInfo(
			Long employeeId,
			EmployeeBasicInfoUpdateRequestDto request
	) {
		User user = userRepository.findByEmployeeIdWithDepartmentAndPosition(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String email = normalizeNullable(request.getEmail());

		if (email != null
				&& userRepository.existsByEmailIgnoreCaseAndEmployeeIdNot(email, employeeId)) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}

		user.updateBasicInfo(
				request.getName().trim(),
				normalizeNullable(request.getPhone()),
				email,
				normalizeNullable(request.getZipcode()),
				normalizeNullable(request.getAddress()),
				normalizeNullable(request.getAddressDetail())
		);

		List<String> roles = employeeRoleService.findRoleCodes(user.getEmployeeId());

		return toEmployeeDetailResponse(user, roles);
	}

	public EmployeeDetailResponseDto updateEmploymentInfo(
			Long employeeId,
			EmployeeEmploymentInfoUpdateRequestDto request
	) {
		User user = userRepository.findByEmployeeIdWithDepartmentAndPosition(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String changeReason = normalizeNullable(request.getChangeReason());

		Department newDepartment = findDepartmentForEmploymentInfo(request);
		Position newPosition = findPositionForEmploymentInfo(request);
		UserStatus newStatus = request.getStatus();

		LocalDateTime newHireDate = request.getHireDate().atStartOfDay();
		LocalDateTime newResignationDate = resolveResignationDate(
				newStatus,
				request.getResignationDate()
		);

		User changedBy = findLoginUserOrNull();

		boolean changed = employeeHistoryService.saveEmploymentInfoHistories(
				user,
				changedBy,
				newDepartment,
				newPosition,
				request.getEmploymentType(),
				newStatus,
				newHireDate,
				newResignationDate,
				changeReason
		);

		if (!changed) {
			throw new BusinessException(ErrorCode.NO_EMPLOYEE_INFO_CHANGED);
		}

		user.updateEmploymentInfo(
				newDepartment,
				newPosition,
				request.getEmploymentType(),
				newStatus,
				newHireDate,
				newResignationDate
		);

		List<String> roles = employeeRoleService.findRoleCodes(user.getEmployeeId());

		return toEmployeeDetailResponse(user, roles);
	}

	public EmployeeDetailResponseDto updateRoles(
			Long employeeId,
			EmployeeRoleUpdateRequestDto request
	) {
		User user = userRepository.findByEmployeeIdWithDepartmentAndPosition(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String changeReason = normalizeNullable(request.getChangeReason());

		User changedBy = findLoginUserOrNull();

		List<String> updatedRoleCodes = employeeRoleService.updateEmployeeRoles(
				user,
				changedBy,
				request.getRoleCodes(),
				changeReason
		);

		return toEmployeeDetailResponse(user, updatedRoleCodes);
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

	private Department findDepartmentForEmploymentInfo(EmployeeEmploymentInfoUpdateRequestDto request) {
		if (request.getEmploymentType() == EmploymentType.DAILY) {
			return null;
		}

		if (request.getDepartmentId() == null) {
			throw new BusinessException(ErrorCode.REGULAR_EMPLOYEE_REQUIRES_DEPARTMENT_AND_POSITION);
		}

		return departmentRepository.findById(request.getDepartmentId())
				.orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));
	}

	private Position findPositionForEmploymentInfo(EmployeeEmploymentInfoUpdateRequestDto request) {
		if (request.getEmploymentType() == EmploymentType.DAILY) {
			return null;
		}

		if (request.getPositionId() == null) {
			throw new BusinessException(ErrorCode.REGULAR_EMPLOYEE_REQUIRES_DEPARTMENT_AND_POSITION);
		}

		return positionRepository.findById(request.getPositionId())
				.orElseThrow(() -> new BusinessException(ErrorCode.POSITION_NOT_FOUND));
	}

	private LocalDateTime resolveResignationDate(UserStatus status, LocalDate resignationDate) {
		if (status != UserStatus.RESIGNED) {
			return null;
		}

		LocalDate resolvedDate = resignationDate != null ? resignationDate : LocalDate.now();

		return resolvedDate.atStartOfDay();
	}

	private User findLoginUserOrNull() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			return null;
		}

		String loginId = authentication.getName();

		if ("anonymousUser".equals(loginId)) {
			return null;
		}

		return userRepository.findByLoginId(loginId).orElse(null);
	}

	private void validateDuplicateLoginId(String loginId) {
		if (userRepository.existsByLoginIdIgnoreCase(loginId.trim())) {
			throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
		}
	}

	private void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmailIgnoreCase(email.trim())) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
		}
	}

	private void validateRegularEmployeeRequiredFields(EmployeeCreateRequestDto request) {
		if (request.getDepartmentId() == null || request.getPositionId() == null) {
			throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_CREATE_REQUEST);
		}
	}

	private String generateEmployeeNo() {
		String currentYear = String.valueOf(Year.now().getValue());
		String prefix = EMPLOYEE_NO_PREFIX + currentYear;

		int nextSequence = userRepository
				.findTopByEmployeeNoStartingWithOrderByEmployeeNoDesc(prefix)
				.map(User::getEmployeeNo)
				.map(employeeNo -> employeeNo.replace(prefix, ""))
				.map(Integer::parseInt)
				.orElse(0) + 1;

		if (nextSequence > MAX_EMPLOYEE_NO_SEQUENCE) {
			throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_CREATE_REQUEST);
		}

		String employeeNo = prefix + String.format("%04d", nextSequence);

		if (userRepository.existsByEmployeeNo(employeeNo)) {
			throw new BusinessException(ErrorCode.DUPLICATE_EMPLOYEE_NO);
		}

		return employeeNo;
	}

	private String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}