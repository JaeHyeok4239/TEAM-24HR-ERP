package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyCostDto {

    private String month;

    private BigDecimal totalCost;
}