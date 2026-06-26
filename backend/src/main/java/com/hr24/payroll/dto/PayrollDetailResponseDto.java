package com.hr24.payroll.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDetailResponseDto {

    private Long payrollId;

    private String employeeNo;
    private String employeeName;
    private String departmentName;

    private String payMonth;

    private BigDecimal baseSalary;
        
    private BigDecimal totalPay;
    private BigDecimal totalDeduction;
    private BigDecimal netSalary;

    private List<PayrollDetailItemDto> details;
}