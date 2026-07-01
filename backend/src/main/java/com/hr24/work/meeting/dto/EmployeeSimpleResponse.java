package com.hr24.work.meeting.dto;

import com.hr24.employee.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmployeeSimpleResponse {

    private final Long employeeId;
    private final String name;
    private final String departmentName;

    public static EmployeeSimpleResponse from(User user) {
        return new EmployeeSimpleResponse(
            user.getEmployeeId(),
            user.getName(),
            user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null
        );
    }
}
