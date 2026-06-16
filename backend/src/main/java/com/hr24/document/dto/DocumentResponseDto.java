package com.hr24.document.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;
import com.hr24.employee.entity.User;

import lombok.Builder;
import lombok.Getter;

//문서 조회(응답)용 Dto
public class DocumentResponseDto {
	//문서 작성
	
	//문서 목록
	@Getter
    @Builder
	public static class DocumentListDto{
		private Long documentId;
		private Long documentType;
		private Long requesterId;
		private Long processorId;
		private String requester;
		private String processor;
		private String documentTitle;
		private String status;
		private Integer currentStep;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createdAt;
		//from(A) : A로부터 1:1 변환
		public static DocumentListDto from(Document document) {
			return DocumentListDto.builder()
					.documentId(document.getDocumentId())
					.documentType(document.getDocumentType().getTypeId())
					.requesterId(document.getRequester().getEmployeeId())
					.processorId(document.getProcessor() != null ? document.getProcessor().getEmployeeId() : null)
					.requester(document.getRequester().getName())
					.processor(document.getProcessor() != null ? document.getProcessor().getName() : null)
					.documentTitle(document.getDocumentTitle())
					.status(document.getStatus())
					.currentStep(document.getCurrentStep())
					.createdAt(document.getCreatedAt())
					.build();
		}
	}
	
	//Map -> List 변환 메소드
	private static List<DocumentContentDto> toContentList(Map<String, Object> contentMap) {
	    if (contentMap == null || contentMap.isEmpty()) {
	        return Collections.emptyList();
	    }
	    return contentMap.entrySet().stream()
	            .map(entry -> DocumentContentDto.builder()
	                    .field(entry.getKey())
	                    .data(entry.getValue())
	                    .build())
	            .toList();
	}
	
	@Getter
	@Builder
	public static class DocumentContentDto {
	    private String field;
	    private Object data;
	}
	
	//문서 상세
	@Getter
    @Builder
	public static class DocumentDto{
		private Long documentId;
		private String documentType;
		private String documentTitle;
		private String documentStatus;
		private String requester;
		private String processor;
		private String rejectReason;
		private List<DocumentContentDto> content;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime requestedAt;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime processedAt;
		private List<DocumentApprovalDto> approvalHistories;
		private List<DocumentFileResponseDto> documentFileList;
		
		//of(A,B...) : 매개변수가 여러 개일 때 합쳐서 하나의 객체로 생성
		public static DocumentDto of(Document document, 
				List<DocumentApprovalDto> approvalHistories, 
				List<DocumentFileResponseDto> documentFileList) {
			return DocumentDto.builder()
					.documentId(document.getDocumentId())
					.documentType(document.getDocumentType().getTypeName())
					.documentTitle(document.getDocumentTitle())
					.documentStatus(document.getStatus())
					.requester(document.getRequester().getName())
					.processor(document.getProcessor() != null ? document.getProcessor().getName() : null)
					.requestedAt(document.getCreatedAt())
					.processedAt(document.getUpdatedAt())
					.rejectReason(document.getRejectReason())
					.content(toContentList(document.getDocumentContent()))
					.approvalHistories(approvalHistories)
					.documentFileList(documentFileList == null ? Collections.emptyList() : documentFileList)
					.build();
		}
	}
	
	//문서 결재이력
	@Getter
    @Builder
	public static class DocumentApprovalDto {
		private Long historyId;
		private Integer stepOrder;
		private Long approverId;
		private String approver;
		private String approvalStatus;
		
		public static DocumentApprovalDto from(ApprovalHistory approvalHistory) {
			
			return DocumentApprovalDto.builder()
			.historyId(approvalHistory.getHistoryId())
			.stepOrder(approvalHistory.getStepOrder())
			.approverId(approvalHistory.getApprover().getEmployeeId())
			.approver(approvalHistory.getApprover().getName())
			.approvalStatus(approvalHistory.getStatus())
			.build();
		}
	}
	
	//문서 첨부파일
	@Getter
    @Builder
	public static class DocumentFileResponseDto{
		private Long docMappingId;
		private Long documentId;
		private Long attachmentId;
		private String fileName;
		private String fileType;
		
		public static DocumentFileResponseDto from(DocumentFile documentFile) {
			return DocumentFileResponseDto.builder()
					.docMappingId(documentFile.getDocMappingId())
					.documentId(documentFile.getDocument().getDocumentId())
					.attachmentId(documentFile.getAttachment().getAttachmentId())
					.fileName(documentFile.getAttachment().getOriginalName())
					.fileType(documentFile.getAttachment().getAttachmentType())
					.build();
		}
	}
	
	//문서 상세(TMP : 임시저장)
	@Getter
    @Builder
	public static class DocumentTmpDto{
		private Long documentId;
		private Long documentType;
		private String documentTitle;
		
		public static DocumentTmpDto from(Document document) {
			return DocumentTmpDto.builder()
					.documentId(document.getDocumentId())
					.documentType(document.getDocumentType().getTypeId())
					.documentTitle(document.getDocumentTitle())
					.build();
		}
	}
	
}
