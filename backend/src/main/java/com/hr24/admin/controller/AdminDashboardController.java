package com.hr24.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.admin.dto.AdminHrEmployeeFlowResponseDto;
import com.hr24.admin.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/hr-employee-flow")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminHrEmployeeFlowResponseDto> getHrEmployeeFlow() {
        return ResponseEntity.ok(adminDashboardService.getHrEmployeeFlow());
    }
}

