package com.hr24.document.dto;

import java.time.LocalDateTime;

import com.hr24.document.entity.Document;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentDto {
	
	private Long documentId;
	private Long documentType;
	private Long requesterId;
	private Long processorId;
	private String documentTitle;
	private String status;
	private Integer currentStep;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime requestedAt;
	private LocalDateTime processedAt;
	private String rejectReason;
	
	public static DocumentDto from(Document document) {
		return DocumentDto.builder()
				.documentId(document.getDocumentId())
				.documentType(document.getDocumentType().getTypeId())
				.requesterId(document.getRequesterId().getEmployeeId())
				.processorId(document.getProcessorId().getEmployeeId())
				.documentTitle(document.getDocumentTitle())
				.status(document.getStatus())
				.currentStep(document.getCurrentStep())
				.createdAt(document.getCreatedAt())
				.updatedAt(document.getUpdatedAt())
				.processedAt(document.getProcessedAt())
				.rejectReason(document.getRejectReason())
				.build();
				
	}
}
