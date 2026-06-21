package com.hr24.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceLogsDaily;

public interface AttendanceLogDailyRepository extends JpaRepository<AttendanceLogsDaily, Long>{
	
}