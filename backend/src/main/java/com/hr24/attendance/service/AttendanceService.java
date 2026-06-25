package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.attendance.dto.AdminAttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.dto.AttendanceResultDto;
import com.hr24.attendance.dto.CorrectionDto;
import com.hr24.attendance.dto.DailyAttendanceDetailResponseDto;
import com.hr24.attendance.dto.DailyAttendanceInputDto;
import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceTimePolicyRepository;
import com.hr24.attendance.repository.WorkplaceRepository;
import com.hr24.attendance.utils.TimeUtils;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.Leave;
import com.hr24.document.repository.LeaveDateRepository;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.service.HrEmployeeQueryService;

import jakarta.persistence.EntityNotFoundException;

import com.hr24.attendance.entity.Workplace;
import com.hr24.attendance.enums.AttendanceStatus;

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
	private final LeaveDateRepository leaveDateRepository;
	private final AttendanceLogDailyRepository attendanceLogDailyRepository;
	private final AttendanceTimePolicyRepository attendanceTimePolicyRepository;
	private final AttendanceThresholdRepository attendanceThresholdRepository;
	private final AttendanceCorrectionRepository attendanceCorrectionRepository;
	private final AttendanceCalculator attendanceCalculator;
	private final HrEmployeeQueryService hrEmployeeQueryService;
	private final AttendanceLogDailyRepository attendanceLogDatilyrepository;
	
	private static final String FIXED_WORKPLACE_NAME = "HQ";
	
	// 시간 관련 API 테스트용 메서드
	private final boolean IS_TEST_MODE = true; 
	public LocalDateTime getCurrentTime() {
	    if (IS_TEST_MODE) {
	        // 년도/월/일/시간/분
	        return LocalDateTime.of(2026, 6, 23, 23, 00); 
	    }
	    return LocalDateTime.now();
	}
	
	// 매일 밤 오후 11시 배치 프로그램
	// status WORK, LATE인 사람들 중 퇴근 안 찍힌 사람 missing 'Y'으로 변경
	@Transactional
	public void processMissingCheckouts() {
		List<AttendanceStatus> targetStatuses = List.of(AttendanceStatus.WORK, AttendanceStatus.LATE);
	    LocalDate targetDate = getCurrentTime().toLocalDate();
	    LocalDateTime now = getCurrentTime();
	    log.info(">>> 디버깅: targetDate={}, targetStatuses={}, now={}", targetDate, targetStatuses, now);
	    
	    int updatedCount = attendanceResultRepository.updateMissingCheckouts(targetDate, targetStatuses, now);
	    
	    log.info("미퇴근 배치 실행 결과 - {}.처리 건수: {}건", targetDate, updatedCount);

	    if (updatedCount == 0) {
	        log.warn("경고: 조건에 맞는 미퇴근 데이터가 없습니다. (날짜: {}, 상태: {})", targetDate, targetStatuses);
	    }
	}
	
	// 매일 오전 6시 배치 프로그램
	// 기존: 모든 직원들의 results 테이블 생성 기본 READY, leave 있을 시 LEAVE
	// 변경: LEAVE는 아예 빼버리고, LEAVE를 제외한 ACTIVE 직원들을 READY로 생성하기
	@Transactional
	public void createDailyAttendanceResults() {
		LocalDate todayDate = getCurrentTime().toLocalDate();
		LocalDateTime todayTimeDate = getCurrentTime();
	
		Set<Long> processedEmployeeIds = attendanceResultRepository.findAllByWorkDate(todayDate)
	            .stream()
	            .map(ar -> ar.getEmployee().getEmployeeId())
	            .collect(Collectors.toSet());
	
		// 대상 직원 리스트 가져오기
	    List<User> activeUsers = userRepository.findAll().stream()
	            .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
	            .filter(user -> !EmploymentType.DAILY.equals(user.getEmploymentType()))
	            .toList();
		
	    // 오늘 휴가 승인된 직원 ID 리스트 미리 추출(leave 테이블에 startDate-endDate 삭제로 인한 로직 삭제)
//	    Set<Long> leaveEmployeeIds = leaveRepository.findAll().stream()
//	            .filter(leave -> "Y".equals(leave.getIsProcessed()))
//	            .filter(leave -> !todayDate.isBefore(leave.getStartDate().toLocalDate())
//	                          && !todayDate.isAfter(leave.getEndDate().toLocalDate()))
//	            .map(leave -> leave.getDocument().getRequester().getEmployeeId())
//	            .collect(Collectors.toSet());

	    // 저장
	    if (!results.isEmpty()) {
	        attendanceResultRepository.saveAll(results);
	        System.out.println(">>> 금일 근태 데이터 생성 완료: " + results.size() + "건");
	    } else {
	        System.out.println(">>> 오늘 새로 생성할 근태 데이터가 없습니다.");
	    }
	    
	   
	 	// 이미 생성된 직원 제외 근태 기록 생성
	    List<AttendanceResult> dailyResults = activeUsers.stream()
	            .filter(user -> !processedEmployeeIds.contains(user.getEmployeeId()))
	            .map(user -> AttendanceResult.builder()
	                    .employee(user)
	                    .workDate(todayDate)
	                    .attendanceStatus(AttendanceStatus.READY)
	                    .isHolidayWork("N")
	                    .isMissingCheckout("N")
	                    .isFixed("N")
	                    .createdAt(todayTimeDate)
	                    .build())
	            .collect(Collectors.toList());

	    if (!dailyResults.isEmpty()) {
	        attendanceResultRepository.saveAll(dailyResults);
	        System.out.println(">>> 금일 근태 데이터 생성 완료: " + dailyResults.size() + "건");
	    } else {
	        System.out.println(">>> 오늘 새로 생성할 근태 데이터가 없습니다.");
	    }
	}
	
	// 필터링 API(직원 타입/부서/이름 혹은 사번/근태 상태)
	@Transactional(readOnly = true)
	public List<EmployeeListResponseDto> findEmployeesWithFilters(
	        EmploymentType type, Long departmentId, String keyword, AttendanceStatus status, LocalDate date) {
	    List<EmployeeListResponseDto> employees = hrEmployeeQueryService.findEmployees(departmentId, UserStatus.ACTIVE, type, keyword);

	    // status 필터링이 필요한 경우에만 수행
	    if (status != null) {
	        LocalDate targetDate = (date != null) ? date : getCurrentTime().toLocalDate();
	        
	        // 해당 상태인 직원 ID들만 가져옴
	        List<Long> targetIds = getEmployeeIdsByStatus(targetDate, status);
	        
	        // employees에서 targetIds에 포함된 직원만 남김
	        return employees.stream()
	                .filter(e -> targetIds.contains(e.getEmployeeId()))
	                .collect(Collectors.toList());
	    }
	    return employees;
	}
	
	// (필터링에서 사용) 특정 상태를 가진 직원들의 목록을 뽑음(ex. 오늘 work인 직원들)
	private List<Long> getEmployeeIdsByStatus(LocalDate targetDate, AttendanceStatus status) {
        List<AttendanceResult> results = attendanceResultRepository.findByWorkDateAndAttendanceStatus(targetDate, status);
        
        return results.stream()
                .map(result -> result.getEmployee().getEmployeeId())
                .distinct()
                .collect(Collectors.toList());
    }
	
	// 일별 근태 상세 조회(상세 패널)
//	@Transactional(readOnly = true)
//	public AttendanceDetailResponseDto getAttendanceDetail(String loginId, Long targetEmployeeId, LocalDate date, boolean isAdmin) {
//	    // 요청자 + 조회 대상 확인
//	    User requester = userRepository.findByLoginId(loginId)
//	            .orElseThrow(() -> new RuntimeException("요청자를 찾을 수 없습니다."));
//	    User targetEmployee = userRepository.findById(targetEmployeeId)
//	    		.orElseThrow(() -> new IllegalAccessException("대상 사원을 찾을 수 없습니다."));
//	    
//	    // 권한 체크
//	    if (!isAdmin && !requester.getEmployeeId().equals(targetEmployeeId)) {
//            throw new AccessDeniedException("본인의 데이터만 조회할 수 있습니다.");
//        }
//	    
//	    // 일용직 체크용
//	    boolean isDaily = (targetEmployee.getEmploymentType() == EmploymentType.DAILY);
//	    
//	    // 데이터 조회(정규직/일용직)
//	    AttendanceResult result = null;
//	    AttendanceLogsDaily dailyLog = null;
//	    List<CorrectionDto> correctionDtos = new ArrayList<>(); // 초기화
//	    
//	    if(isDaily) {
//	    	dailyLog = attendanceLogDailyRepository.findOneByEmployeeIdAndWorkDate(targetEmployeeId, date)
//	    			.orElseThrow(() -> new IllegalArgumentException("해당 날짜의 일용직 근태 기록이 없습니다."));
//	    	
//	    	// 일용직 정정 이력 조회
//    		correctionDtos = attendanceCorrectionRepository.findByCorrectionTargetDaily(dailyLog).stream()
//    	            .map(this::convertToCorrectionDto)
//    	            .collect(Collectors.toList());
//	    }else {
//	    	result = attendanceResultRepository.findByEmployeeAndWorkDate(targetEmployee, date)
//	    			.orElseThrow(() -> new IllegalArgumentException("해당 날짜의 정규직 근태 기록이 없습니다."));
//	    	
//	    	// 정규직 정정 이력 조회
//    		correctionDtos = attendanceCorrectionRepository.findByCorrectionTarget(result).stream()
//    	            .map(this::convertToCorrectionDto)
//    	            .collect(Collectors.toList());
//	    }
//	    
//	    // 시간 계산
//	    long totalWorkTime = isDaily ? TimeUtils.calculateTotalTime(dailyLog) : TimeUtils.calculateTotalTime(result);
//	    long basicWorkTime = TimeUtils.calculateBasicTime(totalWorkTime);
//	    long overtime = Math.max(0, (totalWorkTime - 60) - basicWorkTime);
//	  
//	    // 근태 결과 조회(정규직/일용직 구별)
//	    if (isAdmin) {
//	    	if(isDaily) {
//	    		// 일용직
//	    		return DailyAttendanceDetailResponseDto.builder()
////		    			.status(dailyLog.getAttendanceStatus())
//		    			.checkIn(dailyLog.getCheckInTime())
//		    			.checkOut(dailyLog.getCheckOutTime())
//		    			.totalWorkTime(totalWorkTime)
//		    			.basicWorkTime(basicWorkTime)
//		    			.overtime(overtime)
//		    			.workplaceName(dailyLog.getWorkplace() != null ? dailyLog.getWorkplace().getWorkplaceName() : "미지정")
//		    			.build();
//	    	}else {
//	    		// 정규직 
//		        return AdminAttendanceDetailResponseDto.builder()
//		                .status(result.getAttendanceStatus())
//		                .checkIn(result.getCheckInTime())
//		                .checkOut(result.getCheckOutTime())
//		                .totalWorkTime(totalWorkTime)
//		                .basicWorkTime(basicWorkTime)
//		                .overtime(overtime)
//		                .corrections(correctionDtos)
//		                .userName(result.getEmployee().getName())
//		                .department(result.getEmployee().getDepartment().getDepartmentName())
//		                .userPosition(result.getEmployee().getPosition().getPositionName())
//		                .workplaceName(result.getWorkplace() != null ? result.getWorkplace().getWorkplaceName() : "미지정")
//		                .build();
//	    	}
//    	}
//	    // 사용자 내 근태 현황
//	    return AttendanceDetailResponseDto.builder()
//	            .status(result.getAttendanceStatus())
//	            .checkIn(result.getCheckInTime())
//	            .checkOut(result.getCheckOutTime())
//	            .totalWorkTime(totalWorkTime)
//	            .basicWorkTime(basicWorkTime)
//	            .overtime(overtime)
//	            .build();
//	}

	// CorrectionDto 변환 로직
	private CorrectionDto convertToCorrectionDto(AttendanceCorrection c) {
		Document doc = c.getDocument();
		User processor = doc.getProcessor();
		
	    return CorrectionDto.builder()
	            .correctionType(c.getCorrectionType())
	            .processStatus(convertStatusToLabel(c.getDocument().getStatus()))
	            .requestedAt(c.getDocument().getRequestedAt())
	            .beforeTime(c.getBeforeTime())
	            .afterTime(c.getAfterTime())
	            .managerTeam(processor != null && processor.getDepartment() != null 
	             ? processor.getDepartment().getDepartmentName() : "미정")
	            .managerPosition(processor != null && processor.getPosition() != null 
	             ? processor.getPosition().getPositionName() : "미정")
	            .correctionReason(c.getCorrectionReason())
	            .documentId(doc.getDocumentId())
	            .documentTitle(doc.getDocumentTitle())
	            .build();
	}
	
	// 일용직 정정 이력 저장
	@Transactional
	public void correctDaily(Long logId, DailyCorrectionDto dto) {
	    // 로그 조회
	    AttendanceLog log = attendanceLogRepository.findById(logId)
	            .orElseThrow(() -> new EntityNotFoundException("기록 없음"));

	    // 유효성 검사
	    if (dto.getAfterTime().isAfter(LocalDateTime.now())) {
	        throw new IllegalArgumentException("미래 시간은 입력할 수 없습니다.");
	    }

	    // 수정 전 시간 저장
	    LocalDateTime beforeTime = log.getLogTime();

	    // 데이터 변경(서비스가 직접 값 변경)
	    log.setLogTime(dto.getAfterTime()); 

	    // 정정 테이블에 데이터 저장
	    AttendanceCorrection correction = AttendanceCorrection.builder()
	            .correctionDailyLog(log)
	            .correctionType(log.getLogType()) // IN/OUT
	            .beforeTime(beforeTime)
	            .afterTime(dto.getAfterTime())
	            .correctionReason(dto.getCorrectionReason())
	            .isProcessed("Y")
	            .build();
	            
	    attendanceCorrectionRepository.save(correction);
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
	
	// 일용직 근태 기록 일괄 저장
	@Transactional
	public String saveDailyAttendanceLogs(List<AttendanceRequest> attendanceList) {
		LocalDateTime todayDateTime = getCurrentTime(); // 오늘 날짜, 시간 구하기
		LocalDate todayDate = todayDateTime.toLocalDate();
		
	    // 모든 ID 추출
	    List<Long> empIds = attendanceList.stream()
	        .map(req -> Long.valueOf(req.getEmployeeId()))
	        .collect(Collectors.toList());
	    List<User> foundUsers = userRepository.findAllById(empIds);
	    
	    List<AttendanceLogsDaily> existingLogs = attendanceLogDailyRepository.findByEmployeeIdAndWorkDate(empIds, todayDate);
	    
	    Set<Long> existingEmpIds = existingLogs.stream()
	    		.map(log -> log.getEmployee().getEmployeeId())
	    		.collect(Collectors.toSet());
	    
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
	    
	    // 성공/스킵 count
	    int successCount = 0;
	    int skippedCount = 0;
	    
	    for (AttendanceRequest req : attendanceList) {
	    	Long empId = Long.valueOf(req.getEmployeeId());
	    	
	    	// 중복체크
	    	if(existingEmpIds.contains(empId)) {
	    		log.info(">>> 이미 등록된 사원입니다. ID {}", empId);
	    		skippedCount++;
	    		continue;
	    	}
	    	
	        User user = userMap.get(empId);
	        Workplace workplace = workplaceMap.get(req.getWorkplaceCode());
	        
	        if (user == null || workplace == null) {
	        	log.warn(">>> 유효하지 않은 데이터입니다. ID {}", empId);
	        	continue;
	        }
	        
	        AttendanceLogsDaily log = AttendanceLogsDaily.builder()
	            .employee(user)
	            .workplace(workplace)
	            .checkInTime(req.getCheckInDateTime())
	            .checkOutTime(req.getCheckOutDateTime())
	            .workDate(todayDate)
	            .isAttended("Y")
				.createdAt(todayDateTime)
				.updatedAt(todayDateTime)
	            .build();
	            
	        logs.add(log);
	        successCount++;
	    }
	    
	    attendanceLogDailyRepository.saveAll(logs);
	    return String.format("총 %d건 처리 완료 - 성공 %d, 중복 %d", attendanceList.size(), successCount, skippedCount);
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
	

	
	// 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(String loginId, Double latitude, Double longitude) {
	    validateOperatingTime(); // 시간 검증
	    LocalDateTime currentTime = getCurrentTime();
	    LocalDate today = currentTime.toLocalDate();

	    User user = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));

	    // 위치+거리 검증
	    Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);

	    // 중복 체크
	    AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, today)
	              .orElseThrow(() -> new RuntimeException("오늘 생성된 근태 결과가 없습니다."));

	    if(result.getAttendanceStatus() != AttendanceStatus.READY) {
	        throw new RuntimeException("이미 출근 처리되었습니다.");
	    }

	    // 출근 시간 판정 및 저장
	    String resultStatus = attendanceCalculator.determineCheckInStatus(user, currentTime);
	    
	    result.setAttendanceStatus(AttendanceStatus.valueOf(resultStatus));
	    result.setCheckInTime(currentTime);
	    result.setIsFixed("N");

	    AttendanceLog log = AttendanceLog.builder()
	            .employee(user)
	            .logType("IN")
	            .logTime(currentTime)
	            .latitude(latitude)
	            .longitude(longitude)
	            .isLocationValid("Y")
	            .workplace(matchedWorkplace)
	            .workDate(today)
	            .createdAt(currentTime)
	            .build();
	    attendanceLogRepository.save(log);
	}
	
	// 퇴근 버튼(직원ID, 위도, 경도)
	public void checkOut(String loginId, Double latitude, Double longitude) {
	    validateOperatingTime(); // 시간 검증
	    LocalDateTime currentTime = getCurrentTime();
	    LocalDate today = currentTime.toLocalDate();
	
	    User user = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
	
	    AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, today)
	            .orElseThrow(() -> new RuntimeException("오늘 출근 기록이 없습니다."));
	
	    // 상태 검증
	    AttendanceStatus status = result.getAttendanceStatus();
	
	    if (status == AttendanceStatus.READY) {
	        throw new RuntimeException("출근 처리가 되지 않았습니다. 먼저 출근 버튼을 눌러주세요.");
	    }
	
	    if (result.getCheckOutTime() != null) {
	        throw new RuntimeException("이미 퇴근 처리가 되었습니다.");
	    }
	
	    // 근무 상태가 아니거나 지각이 아닌 경우(반차 제외)
	    if (status != AttendanceStatus.WORK && status != AttendanceStatus.LATE && status != AttendanceStatus.LEAVE) {
	        throw new RuntimeException("현재 퇴근 처리가 가능한 근무 상태가 아닙니다.");
	    }
	
	    // 반차 확인 로직(status가 LEAVE인 경우)
	    if (status == AttendanceStatus.LEAVE) {
	        // LocalDate.now() 대신 위에서 정의한 today 사용
	    	//반차 여부 검증
	        boolean isHalfLeave = !leaveDateRepository.findHalfLeaveByUserAndDate(user, today).isEmpty();
	        
	        // 반차가 아니면 퇴근 불가
	        if (isHalfLeave) {
	            throw new RuntimeException("휴가 중에는 퇴근 처리를 할 수 없습니다.");
	        }
	    }
	
	    // 위치+거리 검증
	    Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);
	
	    // 퇴근 처리
	    String resultStatus = attendanceCalculator.determineCheckoutStatus(user, currentTime);
	    
	    result.setCheckOutTime(currentTime);
	    result.setAttendanceStatus(AttendanceStatus.valueOf(resultStatus));
	    
	    // 로그 저장
	    AttendanceLog log = AttendanceLog.builder()
	            .employee(user)
	            .logType("OUT")
	            .logTime(currentTime)
	            .latitude(latitude)
	            .longitude(longitude)
	            .isLocationValid("Y")
	            .workplace(matchedWorkplace)
	            .workDate(today)
	            .createdAt(currentTime)
	            .build();
	    attendanceLogRepository.save(log);
	}

	// 정규직 1명의 월별 통계 - 근태 상태 횟수 체크
	private AttendanceResponse createGeneralResponse(List<AttendanceResult> monthList) {
        // 카운트 계산
		Map<String, Long> counts = monthList.stream()
			    .collect(Collectors.groupingBy(
			        r -> r.getAttendanceStatus() != null ? r.getAttendanceStatus().name() : "NULL",
			        Collectors.counting()
			    )); 
        // DTO 리스트 변환
        List<AttendanceResultDto> dtoList = monthList.stream()
            .map(AttendanceResultDto::new)
            .collect(Collectors.toList());
        
        // 응답 객체 생성 및 반환
        AttendanceResponse response = new AttendanceResponse();
        response.setWorkCount(counts.getOrDefault(AttendanceStatus.WORK.name(), 0L).intValue());
        response.setLateCount(counts.getOrDefault(AttendanceStatus.LATE.name(), 0L).intValue());
        response.setEarlyLeaveCount(counts.getOrDefault(AttendanceStatus.EARLY_LEAVE.name(), 0L).intValue());
        response.setAbsentCount(counts.getOrDefault(AttendanceStatus.ABSENT.name(), 0L).intValue());
        response.setLeaveCount(counts.getOrDefault(AttendanceStatus.LEAVE.name(), 0L).intValue());
        response.setAttendanceList(dtoList); // 전체 목록

        return response;
    }
	
	// 일용직 1명의 월별 통계 - 근태 상태 횟수 체크
	public AttendanceResponse createDailyWorkerResponse(List<AttendanceResult> monthList) {
		// 카운트 계산
		Map<String, Long> counts = monthList.stream()
			    .collect(Collectors.groupingBy(
			        r -> r.getAttendanceStatus() != null ? r.getAttendanceStatus().name() : "NULL",
			        Collectors.counting()
			    )); 
        // DTO 리스트 변환
        List<AttendanceResultDto> dtoList = monthList.stream()
            .map(AttendanceResultDto::new)
            .collect(Collectors.toList());
        
        // 응답 객체 생성 및 반환
        AttendanceResponse response = new AttendanceResponse();
        response.setWorkCount(counts.getOrDefault(AttendanceStatus.WORK.name(), 0L).intValue());
        response.setAttendanceList(dtoList); // 전체 목록
		
		return response;
	}
	
	// 1명의 월별 통계 - 일반 사용자/관리자
	public AttendanceResponse getMonthlyAttendanceStats(String loginId, YearMonth yearMonth, Long targetEmployeeId, boolean isAdmin) {
		// 요청자 정보 조회
		User requester = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
		
		// 조회할 대상 ID 결정(관리자+target 넘어왔으면 해당 직원 조회, 아닐 시 본인 조회)
		Long targetId = (isAdmin && targetEmployeeId != null) ? targetEmployeeId : requester.getEmployeeId();
		
		// 실제 조회할 유저 조회
		User targetUser = userRepository.findById(targetId)
	            .orElseThrow(() -> new RuntimeException("조회 대상 직원을 찾을 수 없습니다."));
	
		// 날짜 범위 설정(1일~말일)
		LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
	    LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		// 한 달 근태 기록 목록
	    List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeWithUser(
	    	    targetUser, 
	    	    monthStart.toLocalDate(), 
	    	    monthEnd.toLocalDate()
	    	);
	    
	    if (targetUser.getEmploymentType() == EmploymentType.DAILY) {
	        return createDailyWorkerResponse(monthList);
	    }
	    return createGeneralResponse(monthList);
	}
	

	
}