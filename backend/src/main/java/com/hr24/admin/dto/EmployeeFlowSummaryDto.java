package com.hr24.admin.dto;

public record EmployeeFlowSummaryDto(
		long activeCount,
		long joinCount,
		long leaveCount
) {
}
