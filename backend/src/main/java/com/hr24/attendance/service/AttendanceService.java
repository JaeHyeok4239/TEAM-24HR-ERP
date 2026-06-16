package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
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
	
	// 퇴근 버튼(직원ID, 위도, 경도)
	public void checkOut(Long employeeId, Double latitude, Double longitude) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		
		// 중복 체크
		if (attendanceLogRepository.findByEmployeeIdAndWorkDateAndLogType(employeeId, today, "OUT").isPresent()) {
		    throw new RuntimeException("이미 오늘 자 퇴근 기록이 존재합니다.");
		}
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("OUT")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("Y")
				.workDate(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();
		attendanceLogRepository.save(log);
	}
	
	// 내 근태 현황 월별 달력 조회
	public void yearMonth(Long employeeId, YearMonth yearMonth) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		//YearMonth로 시작일 종료일 구하기
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();
		// 한 달 근태 기록 목록
		List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeIdAndWorkDateBetween(employeeId, monthStart, monthEnd);
		
		// (출근/지각/결근) 선언
		int workCount, lateCount, absentCount;
		for(int i=0; i < monthList.size(); i++){
			//변수이름생각해야함...
			//변수 = monthList.get(i);
		}
				
	}
	
	
	
	
	
}