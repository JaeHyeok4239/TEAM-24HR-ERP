package com.hr24.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.approval.entity.ApprovalLine;
import com.hr24.document.entity.DocumentType;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long>{
	
	List<ApprovalLine> findByDocumentTypeOrderByStepOrderAsc(DocumentType documentType);
}
