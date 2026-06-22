package com.hr24.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long>{

	List<AttendanceCorrection> findByCorrectionTarget(AttendanceResult result);
	
}
