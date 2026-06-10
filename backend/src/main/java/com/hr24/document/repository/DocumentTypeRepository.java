package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long>{

}
