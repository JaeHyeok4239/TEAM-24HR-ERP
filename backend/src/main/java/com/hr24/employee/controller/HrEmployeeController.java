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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인사 직원 관리", description = "직원 조회, 등록, 수정, 권한, 민감정보, 인사이력 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/employees")
public class HrEmployeeController {

	private final HrEmployeeQueryService hrEmployeeQueryService;
	private final HrEmployeeCommandService hrEmployeeCommandService;
	private final EmployeeSensitiveInfoService employeeSensitiveInfoService;
	private final EmployeeHistoryService employeeHistoryService;

	@Operation(
	        summary = "직원 목록 조회",
	        description = "부서, 재직상태, 고용형태, 검색어 조건으로 직원 목록을 조회합니다."
	)
	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
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

	@Operation(
	        summary = "직원 등록",
	        description = "신규 직원을 등록하고 기본 사용자 권한을 부여합니다."
	)
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeDetailResponseDto> createEmployee(
			@Valid @RequestBody EmployeeCreateRequestDto request
	) {
		EmployeeDetailResponseDto employee =
				hrEmployeeCommandService.createEmployee(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(employee);
	}

	@Operation(
	        summary = "부서 트리 조회",
	        description = "부서 계층 구조와 부서별 직원 수를 조회합니다."
	)
	@GetMapping("/departments/tree")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<DepartmentTreeResponseDto> findDepartmentTree() {
		DepartmentTreeResponseDto departmentTree =
				hrEmployeeQueryService.findDepartmentTree();

		return ResponseEntity.ok(departmentTree);
	}

	@Operation(
	        summary = "직원 등록/수정 옵션 조회",
	        description = "직원 등록 또는 수정 화면에서 사용할 부서, 직급, 고용형태, 재직상태 등의 옵션 정보를 조회합니다."
	)
	@GetMapping("/form-options")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeFormOptionsResponseDto> findEmployeeFormOptions() {
		EmployeeFormOptionsResponseDto options =
				hrEmployeeQueryService.findEmployeeFormOptions();

		return ResponseEntity.ok(options);
	}

	@Operation(
	        summary = "직원 상세 조회",
	        description = "직원 ID를 기준으로 직원의 기본정보, 인사정보, 권한 정보를 조회합니다."
	)
	@GetMapping("/{employeeId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeDetailResponseDto> findEmployeeDetail(
			@PathVariable("employeeId") Long employeeId
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeQueryService.findEmployeeDetail(employeeId);

		return ResponseEntity.ok(employeeDetail);
	}

	@Operation(
	        summary = "직원 기본정보 수정",
	        description = "직원의 이름, 연락처, 이메일, 주소 등 기본정보를 수정합니다."
	)
	@PatchMapping("/{employeeId}/basic-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeDetailResponseDto> updateBasicInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeBasicInfoUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateBasicInfo(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}

	@Operation(
	        summary = "직원 인사정보 수정",
	        description = "직원의 부서, 직급, 고용형태, 재직상태, 입사일, 퇴사일 등 인사정보를 수정합니다."
	)
	@PatchMapping("/{employeeId}/employment-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeDetailResponseDto> updateEmploymentInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeEmploymentInfoUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateEmploymentInfo(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}

	@Operation(
	        summary = "직원 접근 권한 수정",
	        description = "직원에게 부여된 시스템 접근 권한을 수정합니다."
	)
	@PatchMapping("/{employeeId}/roles")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
	public ResponseEntity<EmployeeDetailResponseDto> updateRoles(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeRoleUpdateRequestDto request
	) {
		EmployeeDetailResponseDto employeeDetail =
				hrEmployeeCommandService.updateRoles(employeeId, request);

		return ResponseEntity.ok(employeeDetail);
	}
	
	@Operation(
	        summary = "직원 민감정보 조회",
	        description = "직원의 주민등록번호, 계좌번호 등 민감정보를 조회합니다."
	)
	@GetMapping("/{employeeId}/sensitive-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<EmployeeSensitiveInfoResponseDto> findSensitiveInfo(
			@PathVariable("employeeId") Long employeeId
	) {
		EmployeeSensitiveInfoResponseDto sensitiveInfo =
				employeeSensitiveInfoService.findSensitiveInfo(employeeId);

		return ResponseEntity.ok(sensitiveInfo);
	}

	@Operation(
	        summary = "직원 민감정보 수정",
	        description = "직원의 주민등록번호, 은행명, 계좌번호, 예금주 정보를 수정합니다."
	)
	@PatchMapping("/{employeeId}/sensitive-info")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
	public ResponseEntity<EmployeeSensitiveInfoResponseDto> updateSensitiveInfo(
			@PathVariable("employeeId") Long employeeId,
			@Valid @RequestBody EmployeeSensitiveInfoUpdateRequestDto request
	) {
		EmployeeSensitiveInfoResponseDto sensitiveInfo =
				employeeSensitiveInfoService.updateSensitiveInfo(employeeId, request);

		return ResponseEntity.ok(sensitiveInfo);
	}
	
	@Operation(
	        summary = "직원 인사이력 조회",
	        description = "직원의 부서, 직급, 고용형태, 재직상태 변경 이력을 조회합니다."
	)
	@GetMapping("/{employeeId}/histories")
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD', 'HR')")
	public ResponseEntity<List<EmployeeHistoryResponseDto>> findHistories(
			@PathVariable("employeeId") Long employeeId
	) {
		List<EmployeeHistoryResponseDto> histories =
				employeeHistoryService.findHistories(employeeId);

		return ResponseEntity.ok(histories);
	}
}
