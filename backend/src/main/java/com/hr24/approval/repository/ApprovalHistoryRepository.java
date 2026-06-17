package com.hr24.approval.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.document.entity.Document;
import com.hr24.employee.entity.User;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

	List<ApprovalHistory> findByDocumentOrderByStepOrderAsc(Document document);

	boolean existsByDocumentAndApprover(Document document, User approver);

	// 새 결재 이력 조회
	@Query("""
			    SELECT ah
			    FROM ApprovalHistory ah
			    JOIN ah.document d
			    WHERE ah.approver.employeeId = :currentId
			     AND ah.status = 'PND'
			     AND ah.stepOrder = d.currentStep
			     order by d.createdAt desc
			""")
	Page<ApprovalHistory> findPendingApprovals(@Param("currentId") Long currentId, Pageable pageable);

	//결재 조회(필터 및 검색)
	@Query("""
		    SELECT ah
		    FROM ApprovalHistory ah
		    JOIN ah.document d
		    WHERE ah.approver.employeeId = :currentId
			  AND ah.status <> 'PND'
		      AND (:status IS NULL OR ah.status = :status)
		      AND (:documentType IS NULL OR d.documentType.typeId = :documentType)
		      AND (:keyword IS NULL 
		      OR LOWER(d.documentTitle) LIKE LOWER(CONCAT(CONCAT('%', :keyword), '%'))
		      OR LOWER(d.requester.name) LIKE LOWER(CONCAT(CONCAT('%', :keyword), '%'))
		      )
		""")
		Page<ApprovalHistory> findApprovalList(
		    @Param("currentId") Long currentId,
		    @Param("status") String status,
		    @Param("documentType") Long documentType,
		    @Param("keyword") String keyword,
		    Pageable pageable
		);

}
