package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService{
	private final AttendanceLogRepository attendanceLogRepository;
	private final AttendanceResultRepository attendanceResultRepository;
	
	// 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(Long employeeId, Double latitude, Double longitude) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		
		// 중복 체크
		if(attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeId, today).isPresent()) {
			throw new RuntimeException("오늘 자 출근 기록이 존재합니다.");
		}
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("IN")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("Y")
				.workDate(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();
		attendanceLogRepository.save(log);
	}
	
}