package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayrollCalculateResponseDto {

    private Long employeeId;

    private String employeeName;

    private String payMonth;

    private BigDecimal totalPay;

    private BigDecimal totalDeduction;

    private BigDecimal netSalary;
}