package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentCostDto {    

    private String departmentName;

    private BigDecimal totalCost;
}