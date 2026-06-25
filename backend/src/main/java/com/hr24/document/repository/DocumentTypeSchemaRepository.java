package com.hr24.document.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hr24.document.entity.DocumentTypeSchema;
import com.hr24.document.entity.DocumentType;

public interface DocumentTypeSchemaRepository extends JpaRepository<DocumentTypeSchema, Long>{
	
	Optional<DocumentTypeSchema> findByDocumentType(DocumentType documentType);
	
	boolean existsByDocumentType(DocumentType documentType);
}
