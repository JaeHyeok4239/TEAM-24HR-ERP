package com.hr24.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceTimePolicy;

public interface AttendanceTimePolicyRepository extends JpaRepository<AttendanceTimePolicy, Long> {
	// 특정 직원 타입의 출퇴근 정책 가져오기
	Optional<AttendanceTimePolicy> findByEmploymentTypeAndPolicyType(String employmentType, String policyType);
}