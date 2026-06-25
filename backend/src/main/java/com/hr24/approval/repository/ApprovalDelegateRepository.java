package com.hr24.approval.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.approval.entity.ApprovalDelegate;

public interface ApprovalDelegateRepository extends JpaRepository<ApprovalDelegate, Long>{
	
	// 현재 시간이 위임 기간 내에 있고, 활성화('Y') 상태인 대리 결재 설정 조회
	@Query("SELECT d FROM ApprovalDelegate d " +
		       "WHERE d.approver.employeeId = :approverId " +
		       "AND d.approvalLine.approvalLineId = :approvalLineId " +
		       "AND :today BETWEEN d.startDate AND d.endDate " +
		       "AND d.isActive = 'Y'")
		Optional<ApprovalDelegate> findActiveDelegate(
		    @Param("approverId") Long approverId,
		    @Param("approvalLineId") Long approvalLineId,
		    @Param("today") LocalDate today
		);

	// 중복 확인 - 같은 결재자 + 같은 결재선 + 기간 겹치는 활성 대리결재
	@Query("SELECT COUNT(d) > 0 FROM ApprovalDelegate d " +
	       "WHERE d.approver.employeeId = :approverId " +
	       "AND d.approvalLine.approvalLineId = :approvalLineId " +
	       "AND d.isActive = 'Y' " +
	       "AND d.startDate <= :endDate AND d.endDate >= :startDate")
	boolean existsOverlappingDelegate(
	    @Param("approverId") Long approverId,
	    @Param("approvalLineId") Long approvalLineId,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate
	);
    
}
