package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PayrollResponseDto {

    private Long payrollId;

    private String employeeNo;

    private String employeeName;

    private String departmentName;

    private String payMonth;

    private BigDecimal totalPay;

    private BigDecimal totalDeduction;

    private BigDecimal netSalary;

    private String status;
}
