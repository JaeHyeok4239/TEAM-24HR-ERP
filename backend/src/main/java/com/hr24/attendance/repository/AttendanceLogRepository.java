package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceLog;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long>{
	// Log에 Out(퇴근) 있는지 확인
		Optional<AttendanceLog> findByEmployeeIdAndWorkDateAndLogType(Long employeeId, LocalDate workDate, String logType);
}
