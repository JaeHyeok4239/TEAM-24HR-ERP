package com.hr24.document.dto;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.DocumentTypeSchema;

import lombok.Getter;

public class DocumentTypeDto {

    @Getter
    public static class TypeListResponse {
        private Long typeId;
        private String typeName;
        private String detailTable;
        public TypeListResponse(DocumentType documentType) {
            this.typeId = documentType.getTypeId();
            this.typeName = documentType.getTypeName();
            this.detailTable = documentType.getDetailTable();
        }
    }

    @Getter
    public static class SchemaResponse {
        private Long typeId;
        private String typeName;
        private Object schema;

        public SchemaResponse(DocumentTypeSchema s) throws Exception {
            this.typeId = s.getDocumentType().getTypeId();
            this.typeName = s.getDocumentType().getTypeName();
            this.schema = new ObjectMapper().readValue(s.getSchemaJson(), Map.class);
        }
    }
}