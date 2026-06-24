package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.attendance.entity.AttendanceLogsDaily;


public interface AttendanceLogDailyRepository extends JpaRepository<AttendanceLogsDaily, Long>{

	// 리스트 조회
	@Query("SELECT a FROM AttendanceLogsDaily a WHERE a.employee.employeeId IN :empIds AND a.workDate = :workDate")
	List<AttendanceLogsDaily> findByEmployeeIdAndWorkDate(
			@Param("empIds") List<Long> empIds, 
			@Param("workDate") LocalDate workDate);
	
	// 1명 조회
	@Query("SELECT a FROM AttendanceLogsDaily a WHERE a.employee.employeeId = :employeeId AND a.workDate = :workDate")
    Optional<AttendanceLogsDaily> findOneByEmployeeIdAndWorkDate(
            @Param("employeeId") Long employeeId, 
            @Param("workDate") LocalDate workDate);
}