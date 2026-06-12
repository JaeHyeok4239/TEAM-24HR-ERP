package com.hr24.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceLog;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long>{
	
}
