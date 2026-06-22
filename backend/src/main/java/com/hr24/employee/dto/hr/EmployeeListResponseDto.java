package com.hr24.employee.dto.hr;

import com.hr24.employee.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeListResponseDto {

	private Long employeeId;
	private String employeeNo;
	private String name;

	private Long departmentId;
	private String departmentName;

	private Long positionId;
	private String positionName;

	private String status;
	private String email;
	private String phone;

	public static EmployeeListResponseDto from(User user) {
		return new EmployeeListResponseDto(
				user.getEmployeeId(), 
				user.getEmployeeNo(), 
				user.getName(),
				user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null,
				user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null,
				user.getPosition() != null ? user.getPosition().getPositionId() : null,
				user.getPosition() != null ? user.getPosition().getPositionName() : null,
				user.getStatus() != null ? user.getStatus().name() : null, 
				user.getEmail(), 
				user.getPhone());
	}
}