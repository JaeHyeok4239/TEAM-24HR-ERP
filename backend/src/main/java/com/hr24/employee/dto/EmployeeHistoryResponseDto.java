package com.hr24.employee.dto;

import java.time.LocalDateTime;

import com.hr24.employee.entity.EmployeeHistory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeHistoryResponseDto {

	private Long employeeHistoryId;
	private String changeItem;
	private String beforeValue;
	private String afterValue;
	private String changeReason;
	private Long changedByEmployeeId;
	private String changedByName;
	private LocalDateTime createdAt;

	public static EmployeeHistoryResponseDto from(EmployeeHistory history) {
		EmployeeHistoryResponseDto response = new EmployeeHistoryResponseDto();

		response.employeeHistoryId = history.getEmployeeHistoryId();
		response.changeItem = history.getChangeItem() != null
				? history.getChangeItem().name()
				: null;
		response.beforeValue = history.getBeforeValue();
		response.afterValue = history.getAfterValue();
		response.changeReason = history.getChangeReason();

		if (history.getChangedBy() != null) {
			response.changedByEmployeeId = history.getChangedBy().getEmployeeId();
			response.changedByName = history.getChangedBy().getName();
		}

		response.createdAt = history.getCreatedAt();

		return response;
	}
}