package com.hr24.approval.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.document.entity.Document;
import com.hr24.employee.entity.User;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

	List<ApprovalHistory> findByDocumentOrderByStepOrderAsc(Document document);

	boolean existsByDocumentAndApprover(Document document, User approver);

	// 결재 요청함
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

	//결재 보관함
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
	
	
	//처리해야하는 결재 조회 + 검증
	@Query("""
		    SELECT ah
		    FROM ApprovalHistory ah
		    JOIN ah.document d
		    WHERE d.documentId = :documentId
		      AND ah.approver.employeeId = :employeeId
		      AND ah.stepOrder = d.currentStep
		      AND ah.status = 'PND'
		""")
		Optional<ApprovalHistory> findCurrentStepApproval(
		        @Param("documentId") Long documentId,
		        @Param("employeeId") Long employeeId
		);
	
	@Query("SELECT MAX(ah.stepOrder) FROM ApprovalHistory ah WHERE ah.document.documentId = :documentId")
	Integer findMaxStepOrder(@Param("documentId") Long documentId);

	
	//전단계에서 반려 시 미래 단계들 CAN(취소) 상태로 변경
	@Modifying
	@Query("""
	    UPDATE ApprovalHistory ah
	    SET ah.status = 'CAN'
	    WHERE ah.document.documentId = :documentId
	      AND ah.stepOrder > :currentStep
	      AND ah.status = 'PND'
	""")
	void cancelFutureSteps(@Param("documentId") Long documentId, @Param("currentStep") Integer currentStep);
}
