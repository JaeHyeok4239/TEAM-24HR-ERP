package com.hr24.document.dto;

import lombok.Getter;

//문서 관리에 필요한 Dto(request/response 통합)
public class DocumentManagementDto {
	
	@Getter
	public static class DocumentTypeSchemaRequestDto {
		private String schemaJson;
		private Long documentType;
	}
}
