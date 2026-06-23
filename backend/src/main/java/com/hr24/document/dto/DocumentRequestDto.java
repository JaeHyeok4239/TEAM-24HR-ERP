package com.hr24.document.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class DocumentRequestDto {
//	@Getter
//	@Setter
//	public static class DocumentContentDto {
//		private String field;
//		private Object data;
//	}
	
	@Getter
	@Setter
	public static class DocumentDto {
		private String documentTitle;
		private Long documentType;
		private Long requester;
		private String status;
		private Map<String, Object> documentContent;
		private List<Long> attachmentIds;
	}
	
}
