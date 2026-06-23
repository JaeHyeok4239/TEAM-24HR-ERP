package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryUpdateRequestDto {

    private BigDecimal baseSalary;
}