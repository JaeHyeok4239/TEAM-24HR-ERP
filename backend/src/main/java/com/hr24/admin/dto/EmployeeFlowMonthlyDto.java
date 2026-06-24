package com.hr24.admin.dto;

public record EmployeeFlowMonthlyDto(
        String month,
        String monthLabel,
        long regularJoinCount,
        long regularLeaveCount,
        long dailyJoinCount,
        long dailyLeaveCount
) {
}
