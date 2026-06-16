package com.hr24.approval.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.document.entity.Document;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long>{

	List<ApprovalHistory> findByDocumentOrderByStepOrderAsc(Document document);
}
