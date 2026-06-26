package com.hr24.document.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.document.dto.DocumentManagementDto;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.DocumentTypeSchema;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.DocumentTypeSchemaRepository;
import com.hr24.document.repository.LeaveTypeRepository;

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
	    DocumentType documentType = DocumentType.builder()
	            .typeName(requestDto.getTypeName())
	            .detailTable(requestDto.getDetailTable())
	            .requiredProcessing(requestDto.getRequiredProcessing())
	            .build();
	    documentTypeRepository.save(documentType);
	}

	@Transactional
	public void createDocumentTypeSchema(DocumentManagementDto.DocumentTypeSchemaRequestDto requestDto) {
	    DocumentType documentType = documentTypeRepository.findById(requestDto.getDocumentType())
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서 타입입니다."));

	    // detailTable 없는 문서 타입엔 스키마 못 붙임
	    if (documentType.getDetailTable() == null || documentType.getDetailTable().isBlank()) {
	        throw new IllegalStateException("상세 연결 테이블이 없는 문서 타입에는 스키마를 설정할 수 없습니다.");
	    }

	    // 스키마 중복 확인
	    if (documentTypeSchemaRepository.existsByDocumentType(documentType)) {
	        throw new IllegalStateException("이미 스키마가 존재합니다.");
	    }

	    DocumentTypeSchema schema = DocumentTypeSchema.builder()
	            .documentType(documentType)
	            .schemaJson(requestDto.getSchemaJson())
	            .build();
	    
	    documentTypeSchemaRepository.save(schema);
	}
	
	@Transactional
	public void updateDocumentTypeSchema(Long schemaId, DocumentManagementDto.DocumentTypeSchemaRequestDto requestDto) {
	    DocumentTypeSchema schema = documentTypeSchemaRepository.findById(schemaId)
	            .orElseThrow(() -> new EntityNotFoundException("스키마를 찾을 수 없습니다."));

	    schema.setSchemaJson(requestDto.getSchemaJson());
	    schema.setUpdatedAt(LocalDateTime.now());
	}
	
	public LeaveType createLeaveType(LeaveType leaveType) {

		LeaveType saved = leaveTypeRepository.save(leaveType);

		return saved;
	}
}
