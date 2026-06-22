package com.hr24.employee.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.hr.DepartmentTreeResponseDto;
import com.hr24.employee.dto.hr.EmployeeCreateRequestDto;
import com.hr24.employee.dto.hr.EmployeeDetailResponseDto;
import com.hr24.employee.dto.hr.EmployeeFormOptionsResponseDto;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.service.HrEmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/employees")
public class HrEmployeeController {

    private final HrEmployeeService hrEmployeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EmployeeListResponseDto>> findEmployees(
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "status", required = false) UserStatus status,
            @RequestParam(name = "employmentType", required = false) EmploymentType employmentType,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        List<EmployeeListResponseDto> employees = hrEmployeeService.findEmployees(
                departmentId,
                status,
                employmentType,
                keyword
        );

        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeDetailResponseDto> createEmployee(
            @Valid @RequestBody EmployeeCreateRequestDto request
    ) {
        EmployeeDetailResponseDto employee = hrEmployeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/departments/tree")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<DepartmentTreeResponseDto> findDepartmentTree() {
        DepartmentTreeResponseDto departmentTree = hrEmployeeService.findDepartmentTree();

        return ResponseEntity.ok(departmentTree);
    }
    
    
    @GetMapping("/form-options")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeFormOptionsResponseDto> findEmployeeFormOptions() {
        EmployeeFormOptionsResponseDto options = hrEmployeeService.findEmployeeFormOptions();

        return ResponseEntity.ok(options);
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeDetailResponseDto> findEmployeeDetail(
            @PathVariable("employeeId") Long employeeId
    ) {
        EmployeeDetailResponseDto employeeDetail = hrEmployeeService.findEmployeeDetail(employeeId);

        return ResponseEntity.ok(employeeDetail);
    }
}