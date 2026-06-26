package com.hr24.document.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.DocumentTypeSchema;
import com.hr24.document.repository.DocumentTypeSchemaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentContentValidationService {

    private final DocumentTypeSchemaRepository documentTypeSchemaRepository;
    private final ObjectMapper objectMapper;

    public void validate(DocumentType documentType, Map<String, Object> content) {
        // detailTable 없으면 자유양식 - 검증 스킵
        if (documentType.getDetailTable() == null || documentType.getDetailTable().isBlank()) {
            return;
        }

        DocumentTypeSchema schema = documentTypeSchemaRepository.findByDocumentType(documentType)
                .orElseThrow(() -> new IllegalStateException("스키마가 등록되지 않은 문서 타입입니다."));

        Map<String, Object> schemaMap;
        try {
            schemaMap = objectMapper.readValue(schema.getSchemaJson(), Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("스키마 형식이 올바르지 않습니다.");
        }

        List<Map<String, Object>> fields = (List<Map<String, Object>>) schemaMap.get("fields");
        if (fields == null) return;

        List<String> errors = new ArrayList<>();

        for (Map<String, Object> field : fields) {
            String name = (String) field.get("name");
            boolean required = Boolean.TRUE.equals(field.get("required"));
            String type = (String) field.get("type");

            Object value = content.get(name);

            // 필수 필드 누락 체크
            if (required && (value == null || value.toString().isBlank())) {
                errors.add(name + " 필드는 필수입니다.");
                continue;
            }

            if (value == null) continue;

            // 타입 체크
            boolean typeValid = switch (type) {
                case "string" -> value instanceof String;
                case "number" -> value instanceof Number;
                case "date" -> isValidDate(value.toString());
                case "datetime" -> isValidDateTime(value.toString());
                case "date_list" -> value instanceof List;
                default -> true;
            };

            if (!typeValid) {
                errors.add(name + " 필드의 타입이 올바르지 않습니다. (expected: " + type + ")");
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("문서 내용 검증 실패: " + String.join(", ", errors));
        }
    }

    private boolean isValidDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidDateTime(String value) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (Exception e) {
            try {
                LocalDateTime.parse(value); // ISO 형식도 허용
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }
    
}
