package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.repository.WorkplaceRepository;
import com.hr24.attendance.entity.Workplace;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService{
	private final AttendanceLogRepository attendanceLogRepository;
	private final AttendanceResultRepository attendanceResultRepository;
	private final WorkplaceRepository workplaceRepository;
	
	// 매일 밤 오후 11시 배치 프로그램
	// status WORK, LATE인 사람들 중 퇴근 안 찍힌 사람 missing 'Y'으로 변경
	// 오후 11시 ~ 오전 6시 출퇴근 버튼 못 누르게 막기(저장 안 되게)
	public void processMissingCheckouts() {
		
	}
	
	// 매일 오전 6시 배치 프로그램
	// 휴가자 제외 모든 직원들의 results 테이블 생성(상태 READY)
	public void createDailyAttendanceResults() {
		
	}
	
	// 호버사인 계산식
	public static Double LocationUtils(Double user_latitude, Double user_longitude, Double HR_latitude, Double HR_longitude) {
		final int R = 6371; // Radius of the earth in km

        double dLat = Math.toRadians(user_latitude - HR_latitude);
        double dLon = Math.toRadians(user_longitude - HR_longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(HR_latitude)) * Math.cos(Math.toRadians(user_latitude))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km

        return distance*1000;
	}
	
	// 위치 검증
	public Long validateAndGetWorkplace(Double latitude, Double longitude) {
		// 근무지 전체 조회
		var workplaces = workplaceRepository.findAll();

		// 가까운 곳 찾기
		for (Workplace wp : workplaces) {
			double distance = LocationUtils(latitude, longitude, wp.getLatitude(), wp.getLongitude());
		    // 계산된 거리가 허용 반경(radius_meter) 이내인지 확인
		    if (distance <= wp.getRadiusMeter()) {
		        return wp.getWorkplaceId();
		    }
		}
		
		// 못 찾았을 경우
		throw new RuntimeException("근무지 근처가 아닙니다.");
	}
	
	// [1] 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(Long employeeId, Double latitude, Double longitude) {
		// 1. 중복 체크
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		if(attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeId, today).isPresent()) {
			throw new RuntimeException("오늘 자 출근 기록이 존재합니다.");
		}
		// 위의 위치검증 메서드 호출
		Long matchedWorkplaceId = validateAndGetWorkplace(latitude, longitude);
		
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("IN")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("Y")
				.workplaceId(matchedWorkplaceId)
				.workDate(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [2] 퇴근 버튼(직원ID, 위도, 경도)
	public void checkOut(Long employeeId, Double latitude, Double longitude) {
		// 1. 중복 체크
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		if (attendanceLogRepository.findByEmployeeIdAndWorkDateAndLogType(employeeId, today, "OUT").isPresent()) {
		    throw new RuntimeException("이미 오늘 자 퇴근 기록이 존재합니다.");
		}
		// 위의 위치검증 메서드 호출
		Long matchedWorkplaceId = validateAndGetWorkplace(latitude, longitude);
		
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("OUT")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("N")
				.workplaceId(matchedWorkplaceId)
				.workDate(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [3] 내 근태 현황 월별 달력 조회
	public AttendanceResponse yearMonth(Long employeeId, YearMonth yearMonth) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		//YearMonth로 시작일 종료일 구하기
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();
		// 한 달 근태 기록 목록
		List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeIdAndWorkDateBetween(employeeId, monthStart, monthEnd);
		
		// 상태 번호 확인(출근/지각/조퇴/결근/휴가)
		// 원래 statuses 종류 근무/지각/조퇴/결근/휴가
		int workCount = 0;
		int lateCount = 0;
		int earlyLeaveCount = 0;
		int absentCount = 0;
		int leavecount = 0;
		
		// 출근/지각/조퇴/결근/휴가 몇 번 했는지 검사하는 코드
		Map<Long, Long> counts = monthList.stream()
		        .collect(Collectors.groupingBy(AttendanceResult::getAttendanceStatusId, Collectors.counting()));
		    
		    AttendanceResponse response = new AttendanceResponse();
		    response.setWorkCount(counts.getOrDefault(1L, 0L).intValue());
		    response.setLateCount(counts.getOrDefault(2L, 0L).intValue());
		    response.setEarlyLeaveCount(counts.getOrDefault(3L, 0L).intValue());
		    response.setAbsentCount(counts.getOrDefault(4L, 0L).intValue());
		    response.setLeaveCount(counts.getOrDefault(5L, 0L).intValue());
		    response.setAttendance(monthList);

		return response;
	}
	
}