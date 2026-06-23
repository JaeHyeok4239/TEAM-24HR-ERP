package com.hr24.employee.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.EmployeeHistoryResponseDto;
import com.hr24.employee.dto.hr.DepartmentTreeResponseDto;
import com.hr24.employee.dto.hr.EmployeeBasicInfoUpdateRequestDto;
import com.hr24.employee.dto.hr.EmployeeCreateRequestDto;
import com.hr24.employee.dto.hr.EmployeeDetailResponseDto;
import com.hr24.employee.dto.hr.EmployeeEmploymentInfoUpdateRequestDto;
import com.hr24.employee.dto.hr.EmployeeFormOptionsResponseDto;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.dto.hr.EmployeeRoleUpdateRequestDto;
import com.hr24.employee.dto.hr.EmployeeSensitiveInfoResponseDto;
import com.hr24.employee.dto.hr.EmployeeSensitiveInfoUpdateRequestDto;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.service.EmployeeHistoryService;
import com.hr24.employee.service.EmployeeSensitiveInfoService;
import com.hr24.employee.service.HrEmployeeCommandService;
import com.hr24.employee.service.HrEmployeeQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/employees")
public class HrEmployeeController {

	private final HrEmployeeQueryService hrEmployeeQueryService;
	private final HrEmployeeCommandService hrEmployeeCommandService;
	private final EmployeeSensitiveInfoService employeeSensitiveInfoService;
	private final EmployeeHistoryService employeeHistoryService;

	// 직원 목록 조회
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<List<EmployeeListResponseDto>> findEmployees(
			@RequestParam(name = "departmentId", required = false) Long departmentId,
			@RequestParam(name = "status", required = false) UserStatus status,
			@RequestParam(name = "employmentType", required = false) EmploymentType employmentType,
			@RequestParam(name = "keyword", required = false) String keyword
	) {
		List<EmployeeListResponseDto> employees = hrEmployeeQueryService.findEmployees(
				departmentId,
				status,
				employmentType,
				keyword
		);

		return ResponseEntity.ok(employees);
	}

	// 직원 등록
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<EmployeeDetailResponseDto> createEmployee(
			@Valid @RequestBody EmployeeCreateRequestDto request
	) {
		EmployeeDetailResponseDto employee =
				hrEmployeeCommandService.createEmployee(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(employee);
	}

	// 부서 트리 및 부서별 직원 수 조회
	@GetMapping("/departments/tree")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<DepartmentTreeResponseDto> findDepartmentTree() {
		DepartmentTreeResponseDto departmentTree =
				hrEmployeeQueryService.findDepartmentTree();

		return ResponseEntity.ok(departmentTree);
	}

	// 직원 등록/수정 폼 옵션 조회
	@GetMapping("/form-options")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<EmployeeFormOptionsResponseDto> findEmployeeFormOptions() {
		EmployeeFormOptionsResponseDto options =
				hrEmployeeQueryService.findEmployeeFormOptions();

		return ResponseEntity.ok(options);
	}

	// 직원 상세 조회
	@GetMapping("/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<EmployeeDetailResponseDto> findEmployeeDetail(
			@PathVariable("employeeId") Long employeeId
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeQueryService.findEmployeeDetail(employeeId);

		return ResponseEntity.ok(employeeDetail);
	}

	// 직원 기본정보 수정
	@PatchMapping("/{employeeId}/basic-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<EmployeeDetailResponseDto> updateBasicInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeBasicInfoUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateBasicInfo(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}

	// 직원 인사정보 수정
	@PatchMapping("/{employeeId}/employment-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<EmployeeDetailResponseDto> updateEmploymentInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeEmploymentInfoUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateEmploymentInfo(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}

	// 직원 접근 권한 수정
	@PatchMapping("/{employeeId}/roles")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
	public ResponseEntity<EmployeeDetailResponseDto> updateRoles(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeRoleUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateRoles(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}
	
	// 민감 정보 조회
	@GetMapping("/{employeeId}/sensitive-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
	public ResponseEntity<EmployeeSensitiveInfoResponseDto> findSensitiveInfo(
			@PathVariable("employeeId") Long employeeId
	) {
		EmployeeSensitiveInfoResponseDto sensitiveInfo =
				employeeSensitiveInfoService.findSensitiveInfo(employeeId);

		return ResponseEntity.ok(sensitiveInfo);
	}

	// 민감 정보 수정
	@PatchMapping("/{employeeId}/sensitive-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
	public ResponseEntity<EmployeeSensitiveInfoResponseDto> updateSensitiveInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeSensitiveInfoUpdateRequestDto request
	) {
		EmployeeSensitiveInfoResponseDto sensitiveInfo =
				employeeSensitiveInfoService.updateSensitiveInfo(employeeId, request);

		return ResponseEntity.ok(sensitiveInfo);
	}
	
	//인사 이력 조회
	@GetMapping("/{employeeId}/histories")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
	public ResponseEntity<List<EmployeeHistoryResponseDto>> findHistories(
			@PathVariable("employeeId") Long employeeId
	) {
		List<EmployeeHistoryResponseDto> histories =
				employeeHistoryService.findHistories(employeeId);

		return ResponseEntity.ok(histories);
	}
}
