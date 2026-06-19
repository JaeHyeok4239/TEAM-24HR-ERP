package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.entity.AttendanceTimePolicy;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceTimePolicyRepository;
import com.hr24.attendance.repository.WorkplaceRepository;
import com.hr24.document.entity.Leave;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.attendance.entity.Workplace;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService{
	private final AttendanceLogRepository attendanceLogRepository;
	private final AttendanceResultRepository attendanceResultRepository;
	private final WorkplaceRepository workplaceRepository;
	private final UserRepository userRepository;
	private final LeaveRepository leaveRepository; 
	private final AttendanceTimePolicyRepository attendanceTimePolicyrepository;
	private final AttendanceThresholdRepository attendanceThresholdRepository;
	
	// 매일 밤 오후 11시 배치 프로그램
	// status WORK, LATE인 사람들 중 퇴근 안 찍힌 사람 missing 'Y'으로 변경
	@Transactional
	public void processMissingCheckouts() {
		List<Long> attendanceStatusId = List.of(7L, 8L);
		int updatedCount = attendanceResultRepository.updateMissingCheckouts(attendanceStatusId);
		log.info("미퇴근 처리 완료: {}건", updatedCount);
		
	}
	
	// 매일 오전 6시 배치 프로그램
	// 모든 직원들의 results 테이블 생성 READY
	// leave 조회해서 데이터 있을 경우 LEAVE
	@Transactional
	public void createDailyAttendanceResults() {
		LocalDate todayDate = LocalDate.now();
		LocalDateTime todayTimeDate = LocalDateTime.now();
		
		// 오늘 이미 결과가 생성되었는지 확인
		boolean exists = attendanceResultRepository.existsByWorkDate(todayDate.atStartOfDay());
		if (exists) {
		    return; // 이미 데이터가 있으면 종료
		}
		List<User> allUsers = userRepository.findAll();
		
		// Active 직원 필터링
		List<User> activeUsers = allUsers.stream()
				.filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
				.toList();
		
		// 휴가 테이블에 있는 모든 직원들
		List<Leave> leaveUsers = leaveRepository.findAll();
		// 승인+오늘 휴가 신청한 리스트
		Set<Long> leaveEmployeeIds = leaveUsers.stream()
				.filter(leave -> "Y".equals(leave.getIsProcessed()))
				.filter(leave -> !todayDate.isBefore(leave.getStartDate().toLocalDate())
							  && !todayDate.isAfter(leave.getEndDate().toLocalDate()))
				.map(leave -> leave.getDocument().getRequester().getEmployeeId())
				.collect(Collectors.toSet());
		
		List<AttendanceResult> dailyResults = activeUsers.stream()
				.map(user -> {
					// leaveEmployeeIds에 employeeId가 있으면 LEAVE 아니면 READY
					String status = leaveEmployeeIds.contains(user.getEmployeeId()) ? "LEAVE" : "READY";
					// LEAVE면 Y
					String fixedStatus = status.equals("LEAVE") ? "Y" : "N";
					
					return AttendanceResult.builder()
							.employeeId(user.getEmployeeId())
							.workDate(todayDate.atStartOfDay())
							.attendanceStatus(status)
							.isHolidayWork("N")
							.isMissingCheckout("N")
							.isFixed(fixedStatus)
							.createdAt(todayTimeDate)
							.build();
				})
				.collect(Collectors.toList());
				attendanceResultRepository.saveAll(dailyResults);
	}
	
	// 시간 검증(오후 11시~오전 6시 출퇴근 막기)
	private void validateOperatingTime() {
	    LocalTime todayDate = LocalTime.now();
	    LocalTime startTime = LocalTime.of(6, 0);
	    LocalTime endTime = LocalTime.of(23, 0);

	    if (todayDate.isBefore(startTime) || todayDate.isAfter(endTime)) {
	        throw new IllegalStateException("운영 시간이 아닙니다. (06:00 ~ 23:00)");
	    }
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
	
//	// 출근 판정
//	private String determineCheckInStatus(Long employeeId, LocalDateTime logTime) {
//		User user = userRepository.findById(employeeId)
//	            .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
//		
//		// 근무 시작 시간(Policy) 조회
//	    AttendanceTimePolicy policy = attendanceTimePolicyrepository.findByEmploymentTypeAndPolicyType(user.getEmploymentType().name(), "WORK")
//	    		.orElseThrow();
//	    LocalTime startTime = parse(policy.getStartTime());
//	    
//		return;
//	}
//	
//	// 퇴근 판정
//	private String determineCheckoutStatus() {
//		return status;
//	}
	
	// [1] 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(Long employeeId, Double latitude, Double longitude) {
		validateOperatingTime(); // 시간 검증
		LocalDateTime todayDate = LocalDateTime.now(); // 오늘 날짜, 시간 구하기
		LocalDateTime startOfDay = todayDate.toLocalDate().atStartOfDay(); // 00:00:00으로 만듦

		// 중복 체크
		AttendanceResult result = attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeId, startOfDay)
								  .orElseThrow(() -> new RuntimeException("오늘 생성된 근태 결과가 없습니다."));
		
		if(!"READY".equals(result.getAttendanceStatus())) {
			throw new RuntimeException("이미 출근 처리되었습니다.");
		}
		// 위의 위치검증 메서드 호출
		Long matchedWorkplaceId = validateAndGetWorkplace(latitude, longitude);
		
		result.setAttendanceStatus("WORK");
		result.setCheckInTime(todayDate);
		result.setIsFixed("N");
		
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("IN")
				.logTime(todayDate)
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("Y")
				.workplaceId(matchedWorkplaceId)
				.workDate(todayDate)
				.createdAt(todayDate)
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [2] 퇴근 버튼(직원ID, 위도, 경도)
	public void checkOut(Long employeeId, Double latitude, Double longitude) {
		validateOperatingTime(); // 시간 검증
		LocalDateTime todayDate = LocalDateTime.now(); // 오늘 날짜, 시간 구하기
		LocalDateTime startOfDay = todayDate.toLocalDate().atStartOfDay(); // 00:00:00으로 만듦

		// 출근 시 만들어둔 Result를 조회
	    AttendanceResult result = attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeId, startOfDay)
	                              .orElseThrow(() -> new RuntimeException("오늘 출근 기록이 없습니다."));
	    
	    String status = result.getAttendanceStatus();
	    
	    // 퇴근 가능한 상태인지 확인
	    if ("READY".equals(status)) {
	        throw new RuntimeException("출근 처리가 되지 않았습니다. 먼저 출근 버튼을 눌러주세요.");
	    }
	    
	    // 이미 퇴근 처리가 된 경우
	    // out 로그가 있는지 확인 --------------
	    
	    // WORK가 아닌 경우
	    if (!"WORK".equals(status)) {
	        throw new RuntimeException("현재 퇴근 처리가 가능한 근무 상태가 아닙니다.");
	    }
	    
		// 위의 위치검증 메서드 호출
		Long matchedWorkplaceId = validateAndGetWorkplace(latitude, longitude);
		
		AttendanceLog log = AttendanceLog.builder()
				.employeeId(employeeId)
				.logType("OUT")
				.logTime(todayDate)
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("N")
				.workplaceId(matchedWorkplaceId)
				.workDate(todayDate)
				.createdAt(todayDate)
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [3] 내 근태 현황 월별 달력 조회
	public AttendanceResponse yearMonth(Long employeeId, YearMonth yearMonth) {
		// 오늘 날짜 구하기
		LocalDate today = LocalDate.now();
		//YearMonth로 시작일 종료일 구하기
		LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
	    LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
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
		Map<String, Long> counts = monthList.stream()
		        .collect(Collectors.groupingBy(AttendanceResult::getAttendanceStatus, Collectors.counting()));
		    
		    AttendanceResponse response = new AttendanceResponse();
		    response.setWorkCount(counts.getOrDefault("WORK", 0L).intValue());
		    response.setLateCount(counts.getOrDefault("LATE", 0L).intValue());
		    response.setEarlyLeaveCount(counts.getOrDefault("EARLY_LEAVE", 0L).intValue());
		    response.setAbsentCount(counts.getOrDefault("ABSENT", 0L).intValue());
		    response.setLeaveCount(counts.getOrDefault("LEAVE", 0L).intValue());
		    response.setAttendance(monthList);

		return response;
	}
	
}