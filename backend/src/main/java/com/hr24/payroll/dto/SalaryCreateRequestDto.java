package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryCreateRequestDto {

    private Long employeeId;

    private BigDecimal baseSalary;
}