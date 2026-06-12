package com.hr24.document.dto;

import com.hr24.document.entity.DocumentType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentTypeDto {
	
	private Long typeId;
	private String typeName;
	private String detailTable;
	
	public DocumentTypeDto from(DocumentType documentType) {
		return DocumentTypeDto.builder()
				.typeId(documentType.getTypeId())
				.typeName(documentType.getTypeName())
				.detailTable(documentType.getDetailTable())
				.build();
	}
}
