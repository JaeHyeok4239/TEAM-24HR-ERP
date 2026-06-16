package com.hr24.approval.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.document.entity.Document;
import com.hr24.employee.entity.User;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long>{

	List<ApprovalHistory> findByDocumentOrderByStepOrderAsc(Document document);
	
	boolean existsByDocumentAndApprover(Document document, User approver);
}
