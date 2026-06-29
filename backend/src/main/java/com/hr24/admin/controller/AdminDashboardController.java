package com.hr24.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.admin.dto.AdminHrEmployeeFlowResponseDto;
import com.hr24.admin.service.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 대시보드", description = "관리자용 현황 대시보드 API")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(
            summary = "HR 직원 현황 조회",
            description = "관리자 대시보드에서 사용할 직원 입사, 퇴사, 재직 현황 데이터를 조회합니다."
    )
    @GetMapping("/hr-employee-flow")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminHrEmployeeFlowResponseDto> getHrEmployeeFlow() {
        return ResponseEntity.ok(adminDashboardService.getHrEmployeeFlow());
    }
}

