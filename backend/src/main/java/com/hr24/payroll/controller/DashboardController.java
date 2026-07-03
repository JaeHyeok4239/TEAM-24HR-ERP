package com.hr24.payroll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.payroll.dto.DepartmentCostDto;
import com.hr24.payroll.dto.MonthlyCostDto;
import com.hr24.payroll.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/monthly-cost")
    public ResponseEntity<List<MonthlyCostDto>> getMonthlyCost() {

        return ResponseEntity.ok(dashboardService.getMonthlyCost());
    }
    
    
    @GetMapping("/department-cost")
    public ResponseEntity<List<DepartmentCostDto>> getDepartmentCost(
    		@RequestParam("month") String month
    		) {
        return ResponseEntity.ok(dashboardService.getDepartmentCost(month));
    }
}