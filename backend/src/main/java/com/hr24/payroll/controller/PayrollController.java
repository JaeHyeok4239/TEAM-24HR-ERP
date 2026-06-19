package com.hr24.payroll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.service.PayrollService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payrolls")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    public ResponseEntity<List<PayrollResponseDto>> getPayrolls(

            @RequestParam(name = "month", required = false)
            String month,

            @RequestParam(name = "employeeNo", required = false)
            String employeeNo,

            @RequestParam(name = "departmentId", required = false)
            Long departmentId
    ) {

        return ResponseEntity.ok(
                payrollService.searchPayrolls(
                        month,
                        employeeNo,
                        departmentId
                )
        );
    }    
}
