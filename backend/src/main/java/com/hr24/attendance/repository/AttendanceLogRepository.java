package com.hr24.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceLog;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long>{
	// 커스텀 메소드 없음. JPA 기본 저장용으로 쓰임. AttendanceSerivce에서 save때문에 필요함.
}
