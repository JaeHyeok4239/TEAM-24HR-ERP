package com.hr24.document.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.Leave;
import com.hr24.employee.entity.User;

public interface LeaveRepository extends JpaRepository<Leave, Long>{

	// 유저와 날짜로 해당 날짜의 휴가 정보 찾기
    @Query("select l from Leave l " +
           "where l.document.requester = :user " +
           "and :date between l.startDate and l.endDate " +
           "and l.isProcessed = 'Y'")
    Optional<Leave> findByRequesterAndDate(@Param("user") User user, @Param("date") LocalDate date);
    
}
