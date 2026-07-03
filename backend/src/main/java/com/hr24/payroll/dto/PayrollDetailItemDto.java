package com.hr24.payroll.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDetailItemDto {

    private String itemType;
    private String itemName;
    private BigDecimal amount;
}