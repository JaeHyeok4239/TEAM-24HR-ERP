package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.attendance.entity.AttendanceLogDaily;


public interface AttendanceLogDailyRepository extends JpaRepository<AttendanceLogDaily, Long>{
	
	// 특정 날짜 전체 리스트 조회
	List<AttendanceLogDaily> findByWorkDate(LocalDate workDate);

	// 리스트 조회
	@Query("SELECT a FROM AttendanceLogDaily a WHERE a.employee.employeeId IN :empIds AND a.workDate = :workDate")
	List<AttendanceLogDaily> findByEmployeeIdAndWorkDate(
			@Param("empIds") List<Long> empIds, 
			@Param("workDate") LocalDate workDate);
	
	// 1명 조회
	@Query("SELECT a FROM AttendanceLogDaily a WHERE a.employee.employeeId = :employeeId AND a.workDate = :workDate")
    Optional<AttendanceLogDaily> findOneByEmployeeIdAndWorkDate(
            @Param("employeeId") Long employeeId, 
            @Param("workDate") LocalDate workDate);
	
	// 날짜 범위 리스트 조회
	@Query("SELECT a FROM AttendanceLogDaily a JOIN FETCH a.employee WHERE a.workDate BETWEEN :start AND :end")
	List<AttendanceLogDaily> findAllWithEmployeeByWorkDateBetween(
	        @Param("start") LocalDate start, 
	        @Param("end") LocalDate end);
	
	@Query("SELECT a FROM AttendanceLogDaily a " +
	       "WHERE a.employee.employeeId = :employeeId " +
	       "AND FUNCTION('TO_CHAR', a.workDate, 'YYYY-MM') = :yearMonth")
	List<AttendanceLogDaily> findByEmployeeIdAndMonth(@Param("employeeId") Long employeeId, 
	                                                   @Param("yearMonth") String yearMonth);
}