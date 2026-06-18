package com.hr24.employee.dto;

import com.hr24.employee.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentResponseDto {

    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Long parentDepartmentId;
    private String parentDepartmentName;
    private boolean active;

    public static DepartmentResponseDto from(
            Department department
    ) {
        Department parentDepartment =
                department.getParentDepartment();

        return new DepartmentResponseDto(
                department.getDepartmentId(),
                department.getDepartmentCode(),
                department.getDepartmentName(),
                parentDepartment != null
                        ? parentDepartment.getDepartmentId()
                        : null,
                parentDepartment != null
                        ? parentDepartment.getDepartmentName()
                        : null,
                "Y".equals(department.getIsActive())
        );
    }
}