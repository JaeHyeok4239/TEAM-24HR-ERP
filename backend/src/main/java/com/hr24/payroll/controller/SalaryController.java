package com.hr24.payroll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.payroll.dto.SalaryCreateRequestDto;
import com.hr24.payroll.dto.SalaryResponseDto;
import com.hr24.payroll.dto.SalaryUpdateRequestDto;
import com.hr24.payroll.service.SalaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public ResponseEntity<Long> createSalary(

            @RequestBody
            SalaryCreateRequestDto request
    ) {
        Long salaryId = salaryService.createSalary(request);

        return ResponseEntity.ok(salaryId);
    }
    
    
    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateSalary(

            @PathVariable("employeeId")
            Long employeeId,

            @RequestBody
            SalaryUpdateRequestDto request
    ) {

        salaryService.updateSalary(employeeId, request);

        return ResponseEntity.ok("기본급 수정 완료");
    }
    
    
    
    @GetMapping
    public ResponseEntity<List<SalaryResponseDto>> getSalaryList() {

        return ResponseEntity.ok(salaryService.getSalaryList());
    }
}