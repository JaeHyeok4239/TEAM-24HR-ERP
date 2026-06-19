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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/reference-data/departments")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
public class DepartmentController {

    private final DepartmentService departmentService;

    //부서 조회
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>>
            getDepartments() {

        List<DepartmentResponseDto> departments =
                departmentService.findAllDepartments();

        return ResponseEntity.ok(departments);
    }

    //부서 등록
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

    //부서 수정
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