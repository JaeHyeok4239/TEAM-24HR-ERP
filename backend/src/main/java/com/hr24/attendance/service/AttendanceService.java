package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

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
	
	// [1] 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(Long employeeId, Double latitude, Double longitude) {
		// 1. 중복 체크
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		if(attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeId, today).isPresent()) {
			throw new RuntimeException("오늘 자 출근 기록이 존재합니다.");
		}
		
		// 2. 위치 검증
		var workplaces = workplaceRepository.findAll();

		Long matchedWorkplaceId = null;
		boolean isLocationValid = false;
		
		for (Workplace wp : workplaces) {
			double distance = LocationUtils(latitude, longitude, wp.getLatitude(), wp.getLongitude());
		    // 계산된 거리가 허용 반경(radius_meter) 이내인지 확인
		    if (distance <= wp.getRadiusMeter()) {
		        // 이 근무지 ID를 저장
		        matchedWorkplaceId = wp.getWorkplaceId(); 
		        isLocationValid = true;
		        break;
		    }
		}
		
		// 근무지 근처(100미터 이내)가 아닐 경우
		if (!isLocationValid) {
			throw new RuntimeException("근무지 근처가 아닙니다.");
		}
		

		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("IN")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid(isLocationValid ? "Y":"N")
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
		
		// 2. 위치 검증
		var workplaces = workplaceRepository.findAll();

		Long matchedWorkplaceId = null;
		boolean isLocationValid = false;
		
		for (Workplace wp : workplaces) {
			double distance = LocationUtils(latitude, longitude, wp.getLatitude(), wp.getLongitude());
		    // 계산된 거리가 허용 반경(radius_meter) 이내인지 확인
		    if (distance <= wp.getRadiusMeter()) {
		        // 이 근무지 ID를 저장
		        matchedWorkplaceId = wp.getWorkplaceId(); 
		        isLocationValid = true;
		        break;
		    }
		}
		
		// 근무지 근처(100미터 이내)가 아닐 경우
		if (!isLocationValid) {
			throw new RuntimeException("근무지 근처가 아닙니다.");
		}
		
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("OUT")
				.logTime(LocalDateTime.now())
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid(isLocationValid ? "Y":"N")
				.workplaceId(matchedWorkplaceId)
				.workDate(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [3] 내 근태 현황 월별 달력 조회
	public void yearMonth(Long employeeId, YearMonth yearMonth) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		//YearMonth로 시작일 종료일 구하기
		LocalDate monthStart = yearMonth.atDay(1);
		LocalDate monthEnd = yearMonth.atEndOfMonth();
		// 한 달 근태 기록 목록
		List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeIdAndWorkDateBetween(employeeId, monthStart, monthEnd);
		
		// (출근/지각/결근/휴가) 선언 및 초기화 *휴가추가해야함
		int workCount = 0;
		int lateCount = 0;
		int absentCount = 0;
		
		// 출근/지각/결근 몇 번 했는지 검사하는 코드
		for(int i=0; i < monthList.size(); i++){
			AttendanceResult attendance = monthList.get(i);
			// 상태 번호 확인(출근/지각/확인 중 무엇인지)
			if(attendance.getAttendanceStatusId() == 1) {
				workCount++;
			}else if(attendance.getAttendanceStatusId() == 2) {
				lateCount++;
			}else if(attendance.getAttendanceStatusId() == 3) {
				absentCount++;
			}else {
				// 1, 2, 3이 아닌 다른 값이 들어왔을 때 예외 던지기
				throw new IllegalArgumentException("잘못된 근태 상태 ID입니다: " + attendance.getAttendanceStatusId());
			}
		}
		
		// 위 for문 결과 넣고 리턴
		AttendanceResponse response = new AttendanceResponse();
		response.setAbsentCount(absentCount);
		response.setLateCount(lateCount);
		response.setWorkCount(workCount);
		response.setAttendance(monthList);
	}
	
	
	
	
	
}