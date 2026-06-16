package com.hr24.document.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

public class DocumentRequestDto {
	@Getter
	@Setter
	public static class DocumentContentDto {
		private String field;
		private Object data;
	}
	
	@Getter
	@Setter
	public static class DocumentDto {
		private String documentTitle;
		private Long documentType;
		private Long requester;
		private String status;
		private List<DocumentContentDto> documentContent;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime requestAt;
		private List<Long> deleteAttachmentIds;
	}
	
}
