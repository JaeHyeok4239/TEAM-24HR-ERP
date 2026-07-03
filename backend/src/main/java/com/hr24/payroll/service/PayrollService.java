package com.hr24.payroll.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.payroll.dto.PayrollDetailItemDto;
import com.hr24.payroll.dto.PayrollDetailResponseDto;
import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.entity.Payroll;
import com.hr24.payroll.entity.Salary;
import com.hr24.payroll.repository.PayrollDetailRepository;
import com.hr24.payroll.repository.PayrollRepository;
import com.hr24.payroll.repository.SalaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final PayrollDetailRepository payrollDetailRepository;    
    private final SalaryRepository salaryRepository;
    
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
                        .findById(payrollId)
                        .orElseThrow(() -> new RuntimeException("급여 정보 없음"));

        Salary salary = salaryRepository
                        .findByEmployeeEmployeeId(
                                payroll.getUser()
                                       .getEmployeeId()
                        )
                        .orElseThrow(() -> new RuntimeException("기본급 정보 없음"));
        
        List<PayrollDetailItemDto> details = payrollDetailRepository
                        .findByPayrollPayrollId(payrollId)
                        .stream()
                        .map(d -> PayrollDetailItemDto
                                        .builder()
                                        .itemType(d.getItemType())
                                        .itemName(d.getItemName())
                                        .amount(d.getAmount())
                                        .build()
                        )
                        .toList();

        return PayrollDetailResponseDto
                .builder()
                .payrollId(payroll.getPayrollId())
                .employeeNo(
                        payroll.getUser()
                               .getEmployeeNo()
                )
                .employeeName(
                        payroll.getUser()
                               .getName()
                )
                .departmentName(
                        payroll.getUser()
                               .getDepartment()
                               .getDepartmentName()
                )
                .payMonth(payroll.getPayMonth())
                .baseSalary(salary.getBaseSalary())
                .totalPay(payroll.getTotalPay())
                .totalDeduction(payroll.getTotalDeduction())
                .netSalary(payroll.getNetSalary())
                .details(details)
                .build();
    }
}
