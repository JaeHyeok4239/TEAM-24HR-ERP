package com.hr24.approval.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.approval.entity.ApprovalDelegate;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.document.entity.Document;

import lombok.Builder;
import lombok.Getter;


//결재 조회(응답)용 Dto
public class ApprovalResponseDto {
	
	@Getter
	@Builder
	public static class ApprovalHistoryDto {
		private Long historyId;
		private Long documentId;
		private String documentTitle;
		private Integer stepOrder;
		private Long approverId;
		private String approver;
		private String status;
		private String approverComment;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime actedAt;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createdAt;
		private String requester;
		private Long requesterId;
		
		public static ApprovalHistoryDto from(ApprovalHistory approvalHistory) {
			
			Document document = approvalHistory.getDocument();
			
			return ApprovalHistoryDto.builder()
					.historyId(approvalHistory.getHistoryId())
					.documentId(approvalHistory.getDocument().getDocumentId())
					.documentTitle(approvalHistory.getDocument().getDocumentTitle())
					.stepOrder(approvalHistory.getStepOrder())
					.approverId(approvalHistory.getApprover().getEmployeeId())
					.approver(approvalHistory.getApprover().getName())
					.status(approvalHistory.getStatus())
					.approverComment(approvalHistory.getApproverComment())
					.actedAt(approvalHistory.getActedAt())
					.createdAt(approvalHistory.getCreatedAt())
					.requester(document.getRequester().getName())
					.requesterId(document.getRequester().getEmployeeId())
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
		private String approverPosition;
		private Long departmentId;
		private String departmentName;
		
		public static ApprovalLineDto from(ApprovalLine approvalLine) {
			return ApprovalLineDto.builder()
					.approvalLineId(approvalLine.getApprovalLineId())
					.documentType(approvalLine.getDocumentType().getTypeId())
					.documentTypeName(approvalLine.getDocumentType().getTypeName())
					.stepOrder(approvalLine.getStepOrder())
					.defaultApprover(approvalLine.getApprover().getEmployeeId())
					.approverName(approvalLine.getApprover().getName())
					.approverPosition(approvalLine.getApprover().getPosition().getPositionName())
					.departmentId(approvalLine.getDepartment() != null ? approvalLine.getDepartment().getDepartmentId() : null)
					.departmentName(approvalLine.getDepartment() != null ? approvalLine.getDepartment().getDepartmentName() : null)
					.build();
		}
	}
	
	@Getter
	@Builder
	public static class ApprovalDelegateDto {
		private Long approvalDelegateId;
		private Long approverId;
		private String approverName;
		private Long delegateId;
		private String delegateName;
		private LocalDate startDate;
		private LocalDate endDate;
		private String reason;
		private Long approvalLineId;
		private String documentTypeName;
		private String departmentName;
		private String isActive;
		
		public static ApprovalDelegateDto from(ApprovalDelegate approvalDelegate) {
			
			ApprovalLine approvalLine = approvalDelegate.getApprovalLine();
			
			return ApprovalDelegateDto.builder()
		            .approvalDelegateId(approvalDelegate.getApprovalDelegateId())
		            .approverId(approvalDelegate.getApprover().getEmployeeId())
		            .approverName(approvalDelegate.getApprover().getName())
		            .delegateId(approvalDelegate.getDelegate().getEmployeeId())
		            .delegateName(approvalDelegate.getDelegate().getName())
		            .startDate(approvalDelegate.getStartDate())
		            .endDate(approvalDelegate.getEndDate())
		            .reason(approvalDelegate.getReason())
		            .approvalLineId(approvalLine.getApprovalLineId())
		            .documentTypeName(approvalLine.getDocumentType().getTypeName())
		            .departmentName(approvalLine.getDepartment() != null
		                    ? approvalLine.getDepartment().getDepartmentName()
		                    : "공통")
		            .isActive(approvalDelegate.getIsActive())
		            .build();
		}
		
	}
}
