package com.hr24.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLogDaily;
import com.hr24.attendance.entity.AttendanceResult;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long>{
	// 정규직 - 특정 근태 결과에 대한 정정 목록 조회
    List<AttendanceCorrection> findByCorrectionTarget(AttendanceResult result);
    
    // 일용직
    List<AttendanceCorrection> findByCorrectionDailyLog(AttendanceLogDaily target);
    
}
