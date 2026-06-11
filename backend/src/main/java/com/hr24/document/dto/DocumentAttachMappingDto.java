package com.hr24.document.dto;

import com.hr24.document.entity.DocumentAttachMapping;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentAttachMappingDto {
	
	private Long docMappingId;
	private Long documentId;
	private Long attachmentId;
	
	public static DocumentAttachMappingDto from(DocumentAttachMapping documentAttachMapping) {
		return DocumentAttachMappingDto.builder()
				.docMappingId(documentAttachMapping.getDocMappingId())
				.documentId(documentAttachMapping.getDocumentId())
				.attachmentId(documentAttachMapping.getAttachmentId())
				.build();
	}
}
