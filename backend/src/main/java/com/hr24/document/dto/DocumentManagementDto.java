package com.hr24.document.dto;

import lombok.Builder;
import lombok.Getter;

//문서 관리에 필요한 Dto(request/response 통합)
public class DocumentManagementDto {
	
	// 문서 유형 생성 시 요청 dto
	@Getter
	public static class DocumentTypeRequestDto {
	    private String typeName;
	    private String detailTable;
	    private String requiredProcessing;
	}
	
	@Getter
	public static class DocumentTypeSchemaRequestDto {
		private String schemaJson;
		private Long documentType;
	}
	
}
