package com.hr24.document.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.document.dto.DocumentTypeDto;
import com.hr24.document.entity.DocumentTypeSchema;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.DocumentTypeSchemaRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeSchemaRepository documentTypeSchemaRepository;
    private final ObjectMapper objectMapper;

    public List<DocumentTypeDto.TypeListResponse> getTypeList() {
        return documentTypeRepository.findAll()
                .stream()
                .map(DocumentTypeDto.TypeListResponse::new)
                .toList();
    }

    public DocumentTypeDto.SchemaResponse getSchema(Long typeId) {
        DocumentTypeSchema schema = documentTypeSchemaRepository
                .findByDocumentType_TypeId(typeId)
                .orElseThrow(() -> new EntityNotFoundException("스키마를 찾을 수 없습니다"));
        try {
            return new DocumentTypeDto.SchemaResponse(schema);
        } catch (Exception e) {
            e.printStackTrace(); // 추가
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
    }
}