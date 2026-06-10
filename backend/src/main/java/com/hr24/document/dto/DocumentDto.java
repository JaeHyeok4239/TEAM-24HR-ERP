package com.hr24.document.dto;

import java.time.LocalDateTime;

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
	
}
