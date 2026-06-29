package com.hr24.employee.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.department.DepartmentCreateRequestDto;
import com.hr24.employee.dto.department.DepartmentResponseDto;
import com.hr24.employee.dto.department.DepartmentUpdateRequestDto;
import com.hr24.employee.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "부서 관리", description = "HR 기준정보 중 부서 조회, 등록, 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/reference-data/departments")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
            summary = "부서 목록 조회",
            description = "등록된 전체 부서 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>>
            getDepartments() {

        List<DepartmentResponseDto> departments =
                departmentService.findAllDepartments();

        return ResponseEntity.ok(departments);
    }

    @Operation(
            summary = "부서 등록",
            description = "신규 부서를 등록합니다."
    )
    @PostMapping
    public ResponseEntity<DepartmentResponseDto>
            createDepartment(
                    @Valid
                    @RequestBody
                    DepartmentCreateRequestDto requestDto
            ) {

        DepartmentResponseDto responseDto =
                departmentService.createDepartment(requestDto);

        URI location = URI.create(
                "/api/hr/reference-data/departments/"
                        + responseDto.getDepartmentId()
        );

        return ResponseEntity
                .created(location)
                .body(responseDto);
    }

    @Operation(
            summary = "부서 수정",
            description = "부서명, 상위 부서, 사용 여부 등 부서 정보를 수정합니다."
    )
    @PatchMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponseDto>
            updateDepartment(
            		@PathVariable("departmentId") Long departmentId,
                    @Valid
                    @RequestBody
                    DepartmentUpdateRequestDto requestDto
            ) {

        DepartmentResponseDto responseDto =
                departmentService.updateDepartment(
                        departmentId,
                        requestDto
                );

        return ResponseEntity.ok(responseDto);
    }
}