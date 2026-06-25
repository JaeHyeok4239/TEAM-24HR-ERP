package com.hr24.approval.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

public class ApprovalManagementDto {

	@Getter
	public static class ApprovalLineRequestDto {
		private Long documentType;
		private Integer stepOrder;
		private Long departmentId;
		private Long defaultApprover;
		
	}
	
	@Getter
	public static class ApprovalDelegateRequestDto {
		private Long approverId;
		private Long delegateId;
		private String reason;
		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate startDate;
		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate endDate;
		private Long approvalLineId;
	}
}
