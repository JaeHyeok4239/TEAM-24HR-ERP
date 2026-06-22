package com.hr24.attendance.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.attendance.dto.AdminAttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.dto.AttendanceResultDto;
import com.hr24.attendance.dto.CorrectionDto;
import com.hr24.attendance.dto.DailyAttendanceInputDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.entity.AttendanceThreshold;
import com.hr24.attendance.entity.AttendanceTimePolicy;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceTimePolicyRepository;
import com.hr24.attendance.repository.WorkplaceRepository;
import com.hr24.attendance.utils.TimeUtils;
import com.hr24.document.entity.Leave;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.attendance.entity.Workplace;

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
	private final AttendanceLogDailyRepository attendanceLogDailyRepository;
	private final AttendanceTimePolicyRepository attendanceTimePolicyRepository;
	private final AttendanceThresholdRepository attendanceThresholdRepository;
	private final AttendanceCorrectionRepository attendanceCorrectionRepository;
	
	// 시간 관련 API 테스트용 메서드
	private final boolean IS_TEST_MODE = true; 
	private LocalDateTime getCurrentTime() {
	    if (IS_TEST_MODE) {
	        // 년도/월/일/시간/분
	        return LocalDateTime.of(2026, 6, 21, 18,0); 
	    }
	    return LocalDateTime.now();
	}
	
	// 매일 밤 오후 11시 배치 프로그램
	// status WORK, LATE인 사람들 중 퇴근 안 찍힌 사람 missing 'Y'으로 변경
	@Transactional
	public void processMissingCheckouts() {
		//만약 데이터가 수만 건 쌓이면 느려질 수 있으니, work_date = TRUNC(SYSDATE) 조건을 꼭 쿼리에 포함
		List<String> targetStatuses = List.of("WORK", "LATE");
		int updatedCount = attendanceResultRepository.updateMissingCheckouts(targetStatuses);
		log.info("미퇴근 처리 완료: {}건", updatedCount);
		
	}
	
	// 매일 오전 6시 배치 프로그램
	// 모든 직원들의 results 테이블 생성 기본 READY, leave 있을 시 LEAVE
	@Transactional
	public void createDailyAttendanceResults() {
		LocalDate todayDate = getCurrentTime().toLocalDate();
		LocalDateTime todayTimeDate = getCurrentTime();
	
		// 오늘 결과가 생성되어 있는지 확인
		boolean exists = attendanceResultRepository.existsByWorkDate(todayDate.atStartOfDay());
		if (exists) {
			System.out.println(">>> 이미 오늘 데이터가 존재함: " + exists);
		    return; // 데이터가 있으면 종료
		}
		
		// Active+!DAILY 직원 리스트
		List<User> allUsers = userRepository.findAll();
		List<User> activeUsers = allUsers.stream()
				.filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
				.filter(user -> !EmploymentType.DAILY.equals(user.getEmploymentType()))
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
							.employee(user)
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
	
	// 일별 근태 상세 조회
	@Transactional(readOnly = true)
	public AttendanceDetailResponseDto getAttendanceDetail(String loginId, Long targetEmployeeId, LocalDate date, boolean isAdmin) {
	    // 요청자 확인
	    User requester = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new RuntimeException("요청자를 찾을 수 없습니다."));
	    
	    // 조회할 대상 ID(관리자는 타인 조회 가능, 사원은 본인 ID 고정)
	    Long employeeIdToQuery = isAdmin ? targetEmployeeId : requester.getEmployeeId();

	    // 근태 결과 조회
	    AttendanceResult result = attendanceResultRepository.findByEmployeeIdAndWorkDate(employeeIdToQuery, date.atStartOfDay())
	            .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 근태 기록이 없습니다."));

	    // 정정 이력 조회 및 DTO 변환
	    List<CorrectionDto> correctionDtos = attendanceCorrectionRepository.findByCorrectionTarget(result).stream()
	            .map(this::convertToCorrectionDto)
	            .collect(Collectors.toList());

	    // 시간 계산
	    long totalWorkTime = TimeUtils.calculateTotalTime(result);
	    long basicWorkTime = TimeUtils.calculateBasicTime(totalWorkTime);
	    long overtime = Math.max(0, (totalWorkTime - 60) - basicWorkTime);

	    // 결과 반환
	    if (isAdmin) {
	        return AdminAttendanceDetailResponseDto.builder()
	                .status(result.getAttendanceStatus())
	                .checkIn(result.getCheckInTime())
	                .checkOut(result.getCheckOutTime())
	                .totalWorkTime(totalWorkTime)
	                .basicWorkTime(basicWorkTime)
	                .overtime(overtime)
	                .corrections(correctionDtos)
	                .userName(result.getEmployee().getName())
	                .department(result.getEmployee().getDepartment().getDepartmentName())
	                .userPosition(result.getEmployee().getPosition().getPositionName())
	                .workplaceName(result.getWorkplace() != null ? result.getWorkplace().getWorkplaceName() : "미지정")
	                .build();
	    }

	    return AttendanceDetailResponseDto.builder()
	            .status(result.getAttendanceStatus())
	            .checkIn(result.getCheckInTime())
	            .checkOut(result.getCheckOutTime())
	            .totalWorkTime(totalWorkTime)
	            .basicWorkTime(basicWorkTime)
	            .overtime(overtime)
	            .build();
	 
	}

	// CorrectionDto 변환 로직을 별도 메서드로 분리(유지보수 용이)
	private CorrectionDto convertToCorrectionDto(AttendanceCorrection c) {
	    return CorrectionDto.builder()
	            .correctionType(c.getCorrectionType())
	            .processStatus(convertStatusToLabel(c.getDocument().getStatus()))
	            .requestedAt(c.getDocument().getRequestedAt())
	            .beforeTime(c.getBeforeTime())
	            .afterTime(c.getAfterTime())
	            .managerName(c.getDocument().getProcessor() != null ? c.getDocument().getProcessor().getName() : "미정")
	            .remarks(c.getCorrectionReason())
	            .build();
	}
	
    // 상태 코드 변환 메서드
	private String convertStatusToLabel(String status) {
	    if (status == null) return "알 수 없음";

	    return switch (status) {
	        case "TMP" -> "임시 저장";
	        case "REQ" -> "결재 요청";
	        case "APR" -> "승인 완료";
	        case "REJ" -> "반려";
	        case "PRC" -> "처리 중";
	        case "COM" -> "처리 완료";
	        default -> "알 수 없음"; // 정의되지 않은 상태값에 대한 처리
	    };
	}
	
	// 일용직 명단 조회
	public List<DailyAttendanceInputDto> getDailyWorkerList() {
	    return userRepository.findAll().stream()
	        .filter(u -> u.getEmploymentType() == EmploymentType.DAILY)
	        .map(u -> DailyAttendanceInputDto.builder()
	            .employeeId(u.getEmployeeId())
	            .name(u.getName())
	            .employeeNo(u.getEmployeeNo())
	            .build())
	        .collect(Collectors.toList());
	}
	
	// 일용직 근태 기록 추가 저장 배치 프로그램
	@Transactional
	public void saveDailyAttendanceLogs(List<AttendanceRequest> attendanceList) {
		LocalDateTime todayDate = getCurrentTime(); // 오늘 날짜, 시간 구하기
	    // 모든 ID 추출
	    List<Long> empIds = attendanceList.stream()
	        .map(req -> Long.valueOf(req.getEmployeeId()))
	        .collect(Collectors.toList());
	    List<User> foundUsers = userRepository.findAllById(empIds);
	    
	    // DAILY인 모든 사원 Map
	    Map<Long, User> userMap = foundUsers.stream()
	        .filter(user -> user.getEmploymentType() == EmploymentType.DAILY)
	        .collect(Collectors.toMap(User::getEmployeeId, user -> user));

	    List<AttendanceLogsDaily> logs = new ArrayList<>();
	    
	    // workplace 필요한 코드 추출
	    Set<String> workplaceCodes = attendanceList.stream()
	        .map(AttendanceRequest::getWorkplaceCode)
	        .collect(Collectors.toSet());

	    // 조회+TEMP로 시작하는 것만 가져오기
	    Map<String, Workplace> workplaceMap = workplaceRepository
	        .findByWorkplaceCodeInAndWorkplaceCodeStartingWith(workplaceCodes, "TEMP")
	        .stream()
	        .collect(Collectors.toMap(Workplace::getWorkplaceCode, w -> w));
	    
	    for (AttendanceRequest req : attendanceList) {
	    	log.info(">>> 처리 중인 데이터: ID={}, WorkplaceCode={}", req.getEmployeeId(), req.getWorkplaceCode());
	    	Long empId = Long.valueOf(req.getEmployeeId());
	        User user = userMap.get(empId);
	        Workplace workplace = workplaceMap.get(req.getWorkplaceCode());
	        if (user == null) log.warn(">>> 유저를 못 찾음: {}", empId);
	        if (workplace == null) log.warn(">>> 근무지를 못 찾음 (혹은 TEMP가 아님): {}", req.getWorkplaceCode());
	        
	        // 프론트에서 넘어온 데이터(attendanceList)가 userMap에 있는지 확인
	        if (!userMap.containsKey(empId)) {
	            continue; // 일용직이 아닐 시 건너뜀
	        }
	        
	        // 데이터 유효성 검사 (일용직 아님/TEMP가 아님/없음)
	        if (user == null || workplace == null) {
	            continue; 
	        }

	        AttendanceLogsDaily log = AttendanceLogsDaily.builder()
	            .employee(user)
	            .workplace(workplace)
	            .checkInTime(req.getCheckInDateTime())
	            .checkOutTime(req.getCheckOutDateTime())
	            .workDate(req.getCheckInDateTime().toLocalDate())
	            .isAttended("Y")
				.createdAt(todayDate)
				.updatedAt(todayDate)
	            .build();
	            
	        logs.add(log);
	    }
	    
	    attendanceLogDailyRepository.saveAll(logs);
	}
	
	// 시간 검증(오후 11시~오전 6시 출퇴근 막기)
	private void validateOperatingTime() {
	    LocalTime todayDate = getCurrentTime().toLocalTime();
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
	public Workplace validateAndGetWorkplace(Double latitude, Double longitude) {
		// 본사 값만 가져와 저장
		Workplace wp = workplaceRepository.findByWorkplaceCode("HQ")
				.orElseThrow(() -> new RuntimeException("본사 정보를 찾을 수 없습니다."));
		
		double distance = LocationUtils(latitude, longitude, wp.getLatitude(), wp.getLongitude());

	    // 허용 반경(radius_meter) 이내인지 확인
	    if (distance <= wp.getRadiusMeter()) {
	        return wp;
	    }
		throw new RuntimeException("근무지 근처가 아닙니다.");
	}
	
	// 숫자를 시간으로 변환
	private LocalTime convertToLocalTime(Long timeNumber) {
	    if (timeNumber == null) {
	        throw new RuntimeException("시간 정책 값이 설정되지 않았습니다.");
	    }
	    // Long에서 int로 형변환
	    int hours = (int) (timeNumber / 100);
	    int minutes = (int) (timeNumber % 100);
	    
	    return LocalTime.of(hours, minutes);
	}
	
	// 출근 시간 판정
	private String determineCheckInStatus(User user, LocalDateTime logTime) {
	    // 정책 조회(WORK 시작 시간)
	    AttendanceTimePolicy policy = attendanceTimePolicyRepository
	            .findByEmploymentTypeAndPolicyType(user.getEmploymentType().name(), "WORK")
	            .orElseThrow(() -> new RuntimeException("근무 정책이 설정되지 않았습니다."));
	    
	    LocalTime policyStartTime = convertToLocalTime(policy.getStartTime());
	    LocalTime actualTime = logTime.toLocalTime();

	    // 지각/결근 기준 조회
	    AttendanceThreshold lateThreshold = attendanceThresholdRepository
	            .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "LATE")
	            .orElse(null);
	    AttendanceThreshold absenceThreshold = attendanceThresholdRepository
	            .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "ABSENCE")
	            .orElse(null);

	    // 시간 차이 계산(분 단위)
	    long diffMinutes = ChronoUnit.MINUTES.between(policyStartTime, actualTime);
	    log.info(">>> [디버깅] 출근 지연시간(diffMinutes): {}", diffMinutes);
	    log.info(">>> [디버깅] 지각 임계값(lateThreshold): {}", 
	        lateThreshold != null ? lateThreshold.getThresholdMinutes() : "NULL");
	    // 판정 로직
	    // 9시 이전 혹은 9시 출근
	    if (diffMinutes <= 0) {
	        return "WORK"; // 정시 또는 일찍 출근
	    }
	    
	    // 180분(오후 12시)동안 출근 기록이 없을 경우
	    if (absenceThreshold != null && diffMinutes >= absenceThreshold.getThresholdMinutes()) {
	        return "ABSENCE";
	    }
	    
	    // 9시 초과 출근 시 지각
	    if (lateThreshold != null && diffMinutes > lateThreshold.getThresholdMinutes()) {
	        return "LATE";
	    }

	    // 그 외 케이스(나중에 지각을 5분으로 바꾸거나 했을 시 그 시간을 어떻게 처리할 건지에 대한 코드)
	    return "WORK";
	}

	// 퇴근 판정 로직
	private String determineCheckoutStatus(User user, LocalDateTime logTime) {
	    // 정책 조회(WORK 종료 시간)
	    AttendanceTimePolicy policy = attendanceTimePolicyRepository
	            .findByEmploymentTypeAndPolicyType(user.getEmploymentType().name(), "WORK")
	            .orElseThrow(() -> new RuntimeException("근무 정책이 설정되지 않았습니다."));
	    
	    LocalTime policyEndTime = convertToLocalTime(policy.getEndTime());
	    LocalTime actualTime = logTime.toLocalTime();

	    // 조퇴 기준 조회
	    AttendanceThreshold earlyLeaveThreshold = attendanceThresholdRepository
	            .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "EARLY_LEAVE")
	            .orElse(null);

	    // 퇴근 시간이 종료 시간보다 빠른지 비교
	    if (actualTime.isBefore(policyEndTime)) {
	        long diffMinutes = ChronoUnit.MINUTES.between(actualTime, policyEndTime);
	        
	        // 조퇴 기준보다 빨리 퇴근했다면
	        if (earlyLeaveThreshold != null && diffMinutes >= earlyLeaveThreshold.getThresholdMinutes()) {
	            return "EARLY_LEAVE";
	        }
	    }

	    return "OUT"; // 정상 퇴근
	}
	
	// [1] 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(String loginId, Double latitude, Double longitude) {
		validateOperatingTime(); // 시간 검증
		LocalDateTime todayDate = getCurrentTime(); // 오늘 날짜, 시간 구하기
		LocalDateTime startOfDay = todayDate.toLocalDate().atStartOfDay(); // 00:00:00으로 만듦
		
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
		
		// 중복 체크
		AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, startOfDay)
								  .orElseThrow(() -> new RuntimeException("오늘 생성된 근태 결과가 없습니다."));
		
		if(!"READY".equals(result.getAttendanceStatus())) {
			throw new RuntimeException("이미 출근 처리되었습니다.");
		}
		// 위치검증 메서드 호출
		Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);
		// 출근 시간 판정 메서드 호출
		String resultStatus = determineCheckInStatus(user, todayDate);
		
		
		result.setAttendanceStatus(resultStatus);
		result.setCheckInTime(todayDate);
		result.setIsFixed("N");
		
		AttendanceLog log = AttendanceLog.builder()
				.employee(user)
				.logType("IN")
				.logTime(todayDate)
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("Y")
				.workplace(matchedWorkplace)
				.workDate(todayDate)
				.createdAt(todayDate)
				.updatedAt(todayDate)
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [2] 퇴근 버튼(직원ID, 위도, 경도)
	public void checkOut(String loginId, Double latitude, Double longitude) {
		validateOperatingTime(); // 시간 검증
		LocalDateTime todayDate = getCurrentTime(); // 오늘 날짜, 시간 구하기
		LocalDateTime startOfDay = todayDate.toLocalDate().atStartOfDay(); // 00:00:00로 맞추기

		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
		
		// 오늘 작성된 근태가 있는지 확인
	    AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, startOfDay)
	                              .orElseThrow(() -> new RuntimeException("오늘 출근 기록이 없습니다."));
	    
	    String status = result.getAttendanceStatus();
	    
	    // 상태가 READY일 경우(출근 X)
	    if ("READY".equals(status)) {
	        throw new RuntimeException("출근 처리가 되지 않았습니다. 먼저 출근 버튼을 눌러주세요.");
	    }
	    
	    // 이미 퇴근 기록이 있을 경우
	    if (result.getCheckOutTime() != null) {
	    	throw new RuntimeException("이미 퇴근 처리가 되었습니다.");
	    }
	    

	    // 상태가 WORK, LATE가 아닌 경우(EARLY_LEAVE, ABSENT, LEAVE)
	    if (!"WORK".equals(status) && !"LATE".equals(status)) {
	        throw new RuntimeException("현재 퇴근 처리가 가능한 근무 상태가 아닙니다.");
	    }
	    
	    // 상태가 LEAVE인데 반차일 경우
	    if ("LEAVE".equals(status)) {
	        // 오늘자 휴가 정보 가져오기
	        Optional<Leave> leaveOpt = leaveRepository.findByRequesterAndDate(user, LocalDate.now());
	        
	        // 반차라면 퇴근 가능
	        if (leaveOpt.isPresent() && leaveOpt.get().getLeaveType().getLeaveId() == 2L) {
	        } else {
	            // 연차는  퇴근 불가
	            throw new RuntimeException("휴가 중에는 퇴근 처리를 할 수 없습니다.");
	        }
	    }
	 
	    // 위치검증 메서드 호출
 		Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);
		// 출근 시간 판정 메서드 호출
		String resultStatus = determineCheckoutStatus(user, todayDate);
 		
		result.setCheckOutTime(todayDate);
		result.setAttendanceStatus(resultStatus);
		
		AttendanceLog log = AttendanceLog.builder()
				.employee(user)
				.logType("OUT")
				.logTime(todayDate)
				.latitude(latitude)
				.longitude(longitude)
				.isLocationValid("N")
				.workplace(matchedWorkplace)
				.workDate(todayDate)
				.createdAt(todayDate)
				.updatedAt(todayDate)
				.build();
		attendanceLogRepository.save(log);
	}
	
	// [3] 내 근태 현황 월별 달력 조회
	public AttendanceResponse yearMonth(String loginId, YearMonth yearMonth) {
		LocalDate today = getCurrentTime().toLocalDate();
		
		//YearMonth로 시작일 종료일 구하기
		LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
	    LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
	    
	    User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
	    
		// 한 달 근태 기록 목록
		List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeWithUser(user, monthStart, monthEnd);
		
		// 상태 번호 확인(출근/지각/조퇴/결근/휴가)
		int workCount = 0;
		int lateCount = 0;
		int earlyLeaveCount = 0;
		int absentCount = 0;
		int leavecount = 0;
		
		// 엔티티 리스트 DTO 리스트로 변환
		// 출근/지각/조퇴/결근/휴가 몇 번 했는지 검사
		List<AttendanceResultDto> dtoList = monthList.stream()
	            .map(AttendanceResultDto::new) 
	            .collect(Collectors.toList());
		
		// 출근/지각/조퇴/결근/휴가 몇 번 했는지 검사하는 코드
		Map<String, Long> counts = monthList.stream()
		        .collect(Collectors.groupingBy(AttendanceResult::getAttendanceStatus, Collectors.counting()));
		    
		    AttendanceResponse response = new AttendanceResponse();
		    response.setWorkCount(counts.getOrDefault("WORK", 0L).intValue());
		    response.setLateCount(counts.getOrDefault("LATE", 0L).intValue());
		    response.setEarlyLeaveCount(counts.getOrDefault("EARLY_LEAVE", 0L).intValue());
		    response.setAbsentCount(counts.getOrDefault("ABSENT", 0L).intValue());
		    response.setLeaveCount(counts.getOrDefault("LEAVE", 0L).intValue());
		    
		    response.setAttendance(dtoList);

		return response;
	}
	
}