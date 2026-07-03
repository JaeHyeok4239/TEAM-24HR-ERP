package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Long>{

	// 유저와 날짜로 해당 날짜의 휴가 정보 찾기(startDate/endDate 삭제로 인해 사용 x) 
//    @Query("select l from Leave l " +
//           "where l.document.requester = :user " +
//           "and :date between l.startDate and l.endDate " +
//           "and l.isProcessed = 'Y'")
//    Optional<Leave> findByRequesterAndDate(@Param("user") User user, @Param("date") LocalDate date);
//
	
	
}
