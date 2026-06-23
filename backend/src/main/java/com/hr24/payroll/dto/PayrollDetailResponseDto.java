package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayrollDetailResponseDto {

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