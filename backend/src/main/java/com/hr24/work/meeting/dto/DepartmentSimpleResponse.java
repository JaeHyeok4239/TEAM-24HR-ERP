package com.hr24.work.meeting.dto;

import com.hr24.employee.entity.Department;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DepartmentSimpleResponse {

    private final Long departmentId;
    private final String departmentName;

    public static DepartmentSimpleResponse from(Department department) {
        return new DepartmentSimpleResponse(
            department.getDepartmentId(),
            department.getDepartmentName()
        );
    }
}
