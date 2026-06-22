package com.hr24.payroll.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.payroll.dto.PayrollDetailResponseDto;
import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.entity.Payroll;
import com.hr24.payroll.repository.PayrollRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public Page<PayrollResponseDto> searchPayrolls(
    		String month, 
    		String employeeNo, 
    		Long departmentId, 
    		Pageable pageable
    		) {
        return payrollRepository.searchPayrolls(
        		month,
        		employeeNo,
        		departmentId,
        		pageable
        		).map(this::toDto);                
    }

    private PayrollResponseDto toDto(Payroll payroll) {

        return PayrollResponseDto.builder()
                .payrollId(payroll.getPayrollId())
                .employeeNo(payroll.getUser().getEmployeeNo())
                .employeeName(payroll.getUser().getName())
                .departmentName(
                        payroll.getUser()
                                .getDepartment()
                                .getDepartmentName()
                )
                .payMonth(payroll.getPayMonth())
                .totalPay(payroll.getTotalPay())
                .totalDeduction(payroll.getTotalDeduction())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .build();
    }
    
    
    public PayrollDetailResponseDto getPayrollDetail(Long payrollId) {

        Payroll payroll = payrollRepository
                .findDetailById(payrollId)
                .orElseThrow(() -> new RuntimeException("급여 정보를 찾을 수 없습니다."));

        return PayrollDetailResponseDto.builder()
                .payrollId(payroll.getPayrollId())
                .employeeNo(payroll.getUser().getEmployeeNo())
                .employeeName(payroll.getUser().getName())
                .departmentName(
                        payroll.getUser()
                               .getDepartment()
                               .getDepartmentName()
                )
                .payMonth(payroll.getPayMonth())
                .totalPay(payroll.getTotalPay())
                .totalDeduction(payroll.getTotalDeduction())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .build();
    }
}
