package com.hr24.document.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.document.dto.DocumentManagementDto;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.DocumentTypeSchema;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.DocumentTypeSchemaRepository;
import com.hr24.document.repository.LeaveTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentManagementService {

	private final DocumentTypeRepository documentTypeRepository;
	private final DocumentTypeSchemaRepository documentTypeSchemaRepository;
	private final LeaveTypeRepository leaveTypeRepository;

	@Transactional
	public void createDocumentType(DocumentManagementDto.DocumentTypeRequestDto requestDto) {
		DocumentType documentType = DocumentType.builder().typeName(requestDto.getTypeName())
				.detailTable(requestDto.getDetailTable()).requiredProcessing(requestDto.getRequiredProcessing())
				.build();
		documentTypeRepository.save(documentType);
	}

	private final ObjectMapper objectMapper; // 생성자 주입 (@RequiredArgsConstructor 등)

	@Transactional
	public void upsertDocumentTypeSchema(Long typeId, DocumentManagementDto.DocumentTypeSchemaRequestDto requestDto) {
	    DocumentType documentType = documentTypeRepository.findById(typeId)
	            .orElseThrow(() -> new EntityNotFoundException("문서 종류를 찾을 수 없습니다. typeId=" + typeId));

	    String schemaJson;
	    try {
	        Map<String, Object> schemaMap = Map.of("fields", requestDto.getFields());
	        schemaJson = objectMapper.writeValueAsString(schemaMap);
	    } catch (JsonProcessingException e) {
	        throw new IllegalStateException("스키마 JSON 변환 실패", e);
	    }

	    documentTypeSchemaRepository.findByDocumentType_TypeId(typeId).ifPresentOrElse(
	            existing -> existing.updateSchemaJson(schemaJson),
	            () -> {
	                DocumentTypeSchema newSchema = DocumentTypeSchema.builder()
	                        .documentType(documentType)
	                        .schemaJson(schemaJson)
	                        .build();
	                documentTypeSchemaRepository.save(newSchema);
	            });
	}


	public LeaveType createLeaveType(LeaveType leaveType) {

		LeaveType saved = leaveTypeRepository.save(leaveType);

		return saved;
	}
}
