package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.attendance.entity.AttendanceLogsDaily;


public interface AttendanceLogDailyRepository extends JpaRepository<AttendanceLogsDaily, Long>{

	@Query("SELECT a FROM AttendanceLogsDaily a WHERE a.employee.employeeId IN :empIds AND a.workDate = :workDate")
	List<AttendanceLogsDaily> findByEmployeeIdInAndWorkDate(
			@Param("empIds") List<Long> empIds, 
			@Param("workDate") LocalDate workDate);
}