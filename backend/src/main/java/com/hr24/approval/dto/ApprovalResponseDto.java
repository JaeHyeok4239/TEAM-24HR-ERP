package com.hr24.approval.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;

import lombok.Builder;
import lombok.Getter;

//결재 조회(응답)용 Dto
public class ApprovalResponseDto {
	
	@Getter
	@Builder
	public static class ApprovalHistoryDto {
		private Long historyId;
		private Long documentId;
		private Integer stepOrder;
		private Long approverId;
		private String status;
		private String approverComment;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime actedAt;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createdAt;
		public static ApprovalHistoryDto from(ApprovalHistory approvalHistory) {
			return ApprovalHistoryDto.builder()
					.historyId(approvalHistory.getHistoryId())
					.documentId(approvalHistory.getDocument().getDocumentId())
					.stepOrder(approvalHistory.getStepOrder())
					.approverComment(approvalHistory.getApproverComment())
					.actedAt(approvalHistory.getActedAt())
					.createdAt(approvalHistory.getCreateAt())
					.build();
		}
	}
	
	@Getter
	@Builder
	public static class ApprovalLineDto {
		private Long approvalLineId;
		private Long documentType;
		private String documentTypeName;
		private Integer stepOrder;
		private Long defaultApprover;
		private String approverName;
		
		public static ApprovalLineDto from(ApprovalLine approvalLine) {
			return ApprovalLineDto.builder()
					.approvalLineId(approvalLine.getApprovalLineId())
					.documentType(approvalLine.getDocumentType().getTypeId())
					.documentTypeName(approvalLine.getDocumentType().getTypeName())
					.stepOrder(approvalLine.getStepOrder())
					.defaultApprover(approvalLine.getApprover().getEmployeeId())
					.approverName(approvalLine.getApprover().getName())
					.build();
		}
	}
	
	
}
