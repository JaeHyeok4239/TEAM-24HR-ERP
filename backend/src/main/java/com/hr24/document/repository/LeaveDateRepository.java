package com.hr24.document.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.Leave;
import com.hr24.document.entity.LeaveDate;
import com.hr24.employee.entity.User;

public interface LeaveDateRepository extends JpaRepository<LeaveDate, Long> {
    List<LeaveDate> findByLeave(Leave leave);
    
 // LeaveDateRepository
    @Query("""
        SELECT ld FROM LeaveDate ld
        WHERE ld.leave.document.requester = :user
        AND ld.leaveDate = :date
        AND ld.leave.leaveCnt = 0.5
    """)
    Optional<LeaveDate> findHalfLeaveByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);
}