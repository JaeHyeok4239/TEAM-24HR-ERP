package com.hr24.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.approval.entity.ApprovalLine;
import com.hr24.document.entity.DocumentType;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long>{
	
	List<ApprovalLine> findByDocumentTypeOrderByStepOrderAsc(DocumentType documentType);

	@Query("select al from ApprovalLine al " +
		       "where (:documentType is null or al.documentType.typeId = :documentType) " +
		       "and (:keyword is null or al.approver.name like '%' || :keyword || '%') " +
		       "order by al.documentType.typeId, al.stepOrder")
		List<ApprovalLine> search(@Param("documentType") Long documentType, @Param("keyword") String keyword);
	
}
