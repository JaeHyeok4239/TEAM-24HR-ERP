package com.hr24.employee.dto.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyInfoResponseDto {
	
	private Long employeeId;
    private String employeeNo;
    private String loginId;
    private String name;
    
    private String departmentName;
    private String positionName;
    
    private LocalDate hireDate;
    
    private BigDecimal remainingAnnualLeaveDays;
    private BigDecimal totalAnnualLeaveDays;
    
    private String email;
    private String phone;
    
    private String zipcode;
    private String address;
    private String addressDetail;
     
    private List<String> roles;
    private String isFirstLogin;

}
