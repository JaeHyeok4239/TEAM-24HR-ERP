package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.entity.Workplace;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.UserStatus;

public interface AttendanceResultRepository extends JpaRepository<AttendanceResult, Long>{
	
	Optional<AttendanceResult> findByEmployeeAndWorkDate(User employee, LocalDateTime workDate);
	
	// 특정 직원의 특정 날짜 근태 정보+그 직원의 상세 정보
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	           "where ar.employee.employeeId = :employeeId " +
	           "and ar.workDate = :workDate")
	    Optional<AttendanceResult> findByEmployeeIdWithUser(
	        @Param("employeeId") Long employeeId, 
	        @Param("workDate") LocalDateTime workDate
	    );
	
	// 특정 기간 직원 한 명의 근태 조회
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	           "where ar.employee = :employee " +
	           "and ar.workDate between :start and :end")
	    List<AttendanceResult> findByEmployeeWithUser(
	        @Param("employee") User employee, 
	        @Param("start") LocalDateTime start, 
	        @Param("end") LocalDateTime end
	    );
	
	// 특정 기간 모든 직원의 근태 조회
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	       "where ar.workDate between :start and :end")
	List<AttendanceResult> findAllWithEmployeeByWorkDateBetween(
	    @Param("start") LocalDateTime start, 
	    @Param("end") LocalDateTime end
	);
	
	// 마감 배치 프로그램에서 쓰일 qeury문
	@Modifying
	@Query("UPDATE AttendanceResult a " +
		       "SET a.isMissingCheckout = 'Y' " +
		       "WHERE a.checkOutTime IS NULL " +
		       "AND a.attendanceStatus IN :attendanceStatus")
	int updateMissingCheckouts(@Param("attendanceStatus") List<String> attendanceStatus);
	
	// 중복 막기
	boolean existsByWorkDate(LocalDateTime workDate);

	@Query("SELECT ar FROM AttendanceResult ar WHERE ar.employee.employeeId = :employeeId AND ar.workDate = :workDate")
	Optional<AttendanceResult> findByEmployeeIdAndWorkDate(@Param("employeeId") Long employeeId, @Param("workDate") LocalDateTime workDate);
}
