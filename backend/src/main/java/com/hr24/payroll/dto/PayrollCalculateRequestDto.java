package com.hr24.payroll.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollCalculateRequestDto {

    private Long employeeId;

    private String payMonth;
}