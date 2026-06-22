package com.hr24.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceThreshold;

public interface AttendanceThresholdRepository extends JpaRepository<AttendanceThreshold, Long> {
	// 특정 직원 타입의 판정 기준 가져오기
	Optional<AttendanceThreshold> findByEmploymentTypeAndThresholdType(String Employement, String thresholdType);
	
}