package com.hr24.payroll.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.repository.PayrollRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public List<PayrollResponseDto> getPayrolls() {

        return payrollRepository.findAllWithEmployee()
                .stream()
                .map(p -> PayrollResponseDto.builder()
                        .payrollId(p.getPayrollId())
                        .employeeNo(p.getUser().getEmployeeNo())
                        .employeeName(p.getUser().getName())
                        .departmentName(
                                p.getUser()
                                 .getDepartment()
                                 .getDepartmentName())
                        .payMonth(p.getPayMonth())
                        .totalPay(p.getTotalPay())
                        .totalDeduction(p.getTotalDeduction())
                        .netSalary(p.getNetSalary())
                        .status(p.getStatus())
                        .build())
                .toList();
    }
}
