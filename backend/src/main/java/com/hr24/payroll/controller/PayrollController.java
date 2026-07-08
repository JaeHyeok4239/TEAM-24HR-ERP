package com.hr24.payroll.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.payroll.dto.PayrollCalculateRequestDto;
import com.hr24.payroll.dto.PayrollDetailResponseDto;
import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.service.PayrollCalculateService;
import com.hr24.payroll.service.PayrollService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payrolls")
public class PayrollController {
	
    private final PayrollService payrollService;
	private final PayrollCalculateService payrollCalculateService;

    @GetMapping
    public ResponseEntity<Page<PayrollResponseDto>> getPayrolls(

            @RequestParam(name = "month", required = false)
            String month,

            @RequestParam(name = "employeeNo", required = false)
            String employeeNo,

            @RequestParam(name = "departmentId", required = false)
            Long departmentId,
            
            @RequestParam(name = "page", defaultValue = "0")
            int page,

            @RequestParam(name = "size", defaultValue = "10")
            int size
    ) {
    	Pageable pageable = PageRequest.of(
    	                page,
    	                size,
    	                Sort.by(
    	                        Sort.Order.desc("payMonth"),
    	                        Sort.Order.desc("payrollId")
    	                )
    	        );
    	
        return ResponseEntity.ok(payrollService.searchPayrolls(
        		month, 
        		employeeNo, 
        		departmentId, 
        		pageable
        		));
    }
    
    
    @GetMapping("/{payrollId}")
    public ResponseEntity<PayrollDetailResponseDto>
    getPayrollDetail(@PathVariable("payrollId") Long payrollId) {

        return ResponseEntity.ok(payrollService.getPayrollDetail(payrollId));
    }
    
    
    @PostMapping("/calculate")
    public ResponseEntity<PayrollResponseDto>
    calculatePayroll(@RequestBody PayrollCalculateRequestDto request) {

        return ResponseEntity.ok(payrollCalculateService.calculatePayroll(request));
    }
}
