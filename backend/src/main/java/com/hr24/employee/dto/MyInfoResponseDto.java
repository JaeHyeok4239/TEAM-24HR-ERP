package com.hr24.employee.dto;

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
    private List<String> roles;

}
