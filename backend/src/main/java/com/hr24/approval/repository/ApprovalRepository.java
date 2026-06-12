package com.hr24.approval.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.approval.entity.ApprovalHistory;

public interface ApprovalRepository extends JpaRepository<ApprovalHistory, Long>{

}
