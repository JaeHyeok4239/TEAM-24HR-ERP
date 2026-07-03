package com.hr24.admin.dto;

import java.util.List;
import java.util.Map;

public record AdminHrEmployeeFlowResponseDto(
		Map<String, EmployeeFlowSummaryDto> summary,
		List<EmployeeFlowMonthlyDto> monthly
) {
}
