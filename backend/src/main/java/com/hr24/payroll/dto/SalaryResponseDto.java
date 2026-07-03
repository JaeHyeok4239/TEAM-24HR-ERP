package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryResponseDto {

    private Long employeeId;

    private String employeeNo;

    private String employeeName;
    
    private String departmentName;
    
    private Long salaryId;

    private BigDecimal baseSalary;
}
