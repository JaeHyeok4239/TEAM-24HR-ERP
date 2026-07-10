package com.hr24.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.attendance.dto.DailyAttendanceSummaryResponseDto;
import com.hr24.attendance.dto.AttendanceDetailResponseDto;
import com.hr24.attendance.dto.DailyWorkerAttendanceRequestDto;
import com.hr24.attendance.dto.MonthlyAttendanceSummaryResponseDto;
import com.hr24.attendance.dto.AttendanceResultDto;
import com.hr24.attendance.dto.AttendanceSummaryCountDto;
import com.hr24.attendance.dto.CalendarBadgeDto;
import com.hr24.attendance.dto.AttendanceCorrectionRecordDto;
import com.hr24.attendance.dto.DailyAttendanceDetailResponseDto;
import com.hr24.attendance.dto.DailyWorkerDto;
import com.hr24.attendance.dto.DailyWorkerAttendanceManageDto;
import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.dto.AdminMonthlyAttendanceListResponseDto;
import com.hr24.attendance.dto.WorkplaceResponseDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceLogDaily;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.repository.WorkplaceRepository;
import com.hr24.attendance.utils.TimeUtils;
import com.hr24.document.entity.Document;
import com.hr24.document.repository.LeaveDateRepository;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.service.HrEmployeeQueryService;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.work.schedule.repository.HolidayRepository;

import com.hr24.attendance.entity.Workplace;
import com.hr24.attendance.enums.AttendanceStatus;
import com.hr24.attendance.enums.WorkplaceCode;

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
	private final LeaveDateRepository leaveDateRepository;
	private final AttendanceLogDailyRepository attendanceLogDailyRepository;
	private final AttendanceCorrectionRepository attendanceCorrectionRepository;
	private final AttendanceStatusCalculator attendanceStatusCalculator;
	private final HrEmployeeQueryService hrEmployeeQueryService;
	private final HolidayRepository holidayRepository;

	
	// 시간 관련 API 테스트용 메서드
	private final boolean IS_TEST_MODE = false; 
	public LocalDateTime getCurrentTime() {
	    if (IS_TEST_MODE) {
	        // 년도/월/일/시간/분
	        return LocalDateTime.of(2026, 7, 7, 9, 0); 
	    }
	    // 서울 시간대 고정
	    return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
	}
	
	// 모든 근무지 읽어오기
	@Transactional(readOnly = true)
	public List<WorkplaceResponseDto> getWorkplaces() {
	    List<Workplace> workplaceList = workplaceRepository.findAll();

	    return workplaceList.stream()
	            .map(wp -> WorkplaceResponseDto.builder()
	                    .name(wp.getWorkplaceCode().name()) 
	                    .latitude(wp.getLatitude())
	                    .longitude(wp.getLongitude())
	                    .build())
	            .collect(Collectors.toList());
	}
	
	// 매일 밤 오후 11시 배치 프로그램
	// status가 WORK인 직원들의 status를 MISSING_CHECKOUT으로 변경
	@Transactional
	public void processMissingCheckouts() {
		List<AttendanceStatus> targetStatuses = List.of(AttendanceStatus.WORK, AttendanceStatus.LATE);
	    LocalDate targetDate = getCurrentTime().toLocalDate();
	    LocalDateTime now = getCurrentTime();
	    log.info(">>> 미퇴근 처리 배치 시작: targetDate={}, targetStatuses={}", targetDate, targetStatuses);
	    
	    int updatedCount = attendanceResultRepository.updateIsCheckoutMissing(
	        targetDate, targetStatuses, now);
	    
	    log.info("미퇴근 배치 실행 결과 - {}. 처리 건수: {}건", targetDate, updatedCount);

	    if (updatedCount == 0) {
	        log.warn("조건에 맞는 미퇴근 데이터가 없습니다. (날짜: {}, 상태: {})", targetDate, targetStatuses);
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
	    
	 // 대상 직원 필터링(ACTIVE+DAILY 아님+휴가 아님)
	    List<AttendanceResult> results = userRepository.findAll().stream()
	            .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
	            .filter(user -> !EmploymentType.DAILY.equals(user.getEmploymentType()))
	            .filter(user -> !processedEmployeeIds.contains(user.getEmployeeId())) // 이미 생성된 것 제외
	            .map(user -> AttendanceResult.builder()
	            		.employee(user)
	            		.workDate(todayDate)
	            		.attendanceStatus(AttendanceStatus.READY)
	            		.isHolidayWork("N")
						.isCheckoutMissing("N")
						.isFixed("N")
	            		.createdAt(todayTimeDate)
	            		.build())
	            .collect(Collectors.toList());
	    
	    // 저장
	    if (!results.isEmpty()) {
	        attendanceResultRepository.saveAll(results);
	        
	        // 이미 생성된 목록에 추가
	        processedEmployeeIds.addAll(results.stream()
	                .map(r -> r.getEmployee().getEmployeeId())
	                .collect(Collectors.toSet()));
	        
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
						.isCheckoutMissing("N")
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
	
    // 총 근무, 기본 근무, 초과 근무 시간 계산(공휴일 조건)
	public AttendanceDetailResponseDto calculateAttendanceTimes(LocalDateTime checkIn, LocalDateTime checkOut, boolean isHoliday) {
		// 데이터 없을 때 
		if (checkIn == null || checkOut == null) {
	        return AttendanceDetailResponseDto.builder()
	                .checkIn(checkIn)
	                .checkOut(checkOut)
	                .totalWorkTime(0L)
	                .basicWorkTime(0L)
	                .overtime(0L)
	                .build();
	    }
		
		long total = TimeUtils.calculateTotalTime(checkIn, checkOut);
	    long basic = TimeUtils.calculateBasicTime(total, isHoliday);
	    long overtime = TimeUtils.calculateOvertime(total, isHoliday);

	    return AttendanceDetailResponseDto.builder()
	            .checkIn(checkIn)
	            .checkOut(checkOut)
	            .totalWorkTime(total)
	            .basicWorkTime(basic)
	            .overtime(overtime)
	            .build();
	}
	
	// 일별 근태 상세 조회(상세 패널)
	@Transactional(readOnly = true)
	public AttendanceDetailResponseDto getAttendanceDetail(String loginId, Long targetEmployeeId, LocalDate date, boolean isAdmin) {
		
		// 요청자 + 조회 대상 확인
	    User requester = userRepository.findByLoginId(loginId)
	    		.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	    User targetEmployee = userRepository.findById(targetEmployeeId)
	    		.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	    
	    // 부서/직급 null 방어
	    String deptName = (targetEmployee.getDepartment() != null) ? targetEmployee.getDepartment().getDepartmentName() : "미지정";
	    String posName = (targetEmployee.getPosition() != null) ? targetEmployee.getPosition().getPositionName() : "미지정";
	    
	    // 권한 체크
	    if (!isAdmin && !requester.getEmployeeId().equals(targetEmployeeId)) {
            throw new AccessDeniedException("본인의 데이터만 조회할 수 있습니다.");
        }
	    
	    AttendanceLogDaily dailyLog = null;
	    AttendanceResult result = null;
	    
	    // 일용직 체크용
	    boolean isDaily = (targetEmployee.getEmploymentType() == EmploymentType.DAILY);
	    
	    // 데이터 조회용
	    LocalDateTime checkIn = null;
	    LocalDateTime checkOut = null;
	    AttendanceStatus status = null;
	    List<AttendanceCorrectionRecordDto> correctionDtos= new ArrayList<>();
	    String workplaceName = "미지정";
	    boolean isHoliday = holidayRepository.findByHolidayDate(date).isPresent();
	    
	    if (isDaily) {
	        dailyLog = attendanceLogDailyRepository.findOneByEmployeeIdAndWorkDate(targetEmployeeId, date)
	                .orElse(null);
	        
	        if(dailyLog != null) {
	        	checkIn = dailyLog.getCheckInTime();
	 	        checkOut = dailyLog.getCheckOutTime();
	 	        workplaceName = dailyLog.getWorkplace() != null ? dailyLog.getWorkplace().getWorkplaceName() : "미지정";
	 	        correctionDtos = attendanceCorrectionRepository.findByCorrectionDailyLog(dailyLog).stream()
	 	                .map(this::convertToCorrectionDto).collect(Collectors.toList());
	 	        status = "Y".equals(dailyLog.getIsAttended()) ? AttendanceStatus.WORK : AttendanceStatus.READY;
	        } else {
	        	status = AttendanceStatus.READY; // 데이터가 없으면 미출근
	        }
	       
	    } else {
	    	result = attendanceResultRepository.findByEmployeeAndWorkDate(targetEmployee, date).orElse(null);
	    	
	    	if (result != null) {
	        checkIn = result.getCheckInTime();
	        checkOut = result.getCheckOutTime();
	        status = result.getAttendanceStatus();
	        workplaceName = result.getWorkplace() != null ? result.getWorkplace().getWorkplaceName() : "미지정";
	        correctionDtos = attendanceCorrectionRepository.findByCorrectionTarget(result).stream()
	                .map(this::convertToCorrectionDto).collect(Collectors.toList());
	    	}
	    }
	    
	    // 계산 메서드
	    AttendanceDetailResponseDto timeDto = calculateAttendanceTimes(checkIn, checkOut, isHoliday);
	    
	    // 근태 결과 조회(정규직/일용직 구별)
	    if (isAdmin) {
	    	if(isDaily) {
	    		// 일용직
	    		// 수정한 담당자 부서/직급 추가
	    		return DailyAttendanceDetailResponseDto.builder()
	                    .status(status)
	                    .checkIn(checkIn)
	                    .checkOut(checkOut)
	                    .totalWorkTime(timeDto.getTotalWorkTime())
	                    .basicWorkTime(timeDto.getBasicWorkTime())
	                    .overtime(timeDto.getOvertime())
	                    .corrections(correctionDtos)
	                    .userName(targetEmployee.getName())
	                    .department(deptName)
	                    .userPosition(posName)
	                    .workplaceName(workplaceName)
	                    .build();
	    	}else {
	    		// 정규직(일반 사용자와 동일)
	    		return AttendanceDetailResponseDto.builder()
	                    .status(status)
	                    .checkIn(checkIn)
	                    .checkOut(checkOut)
	                    .totalWorkTime(timeDto.getTotalWorkTime())
	                    .basicWorkTime(timeDto.getBasicWorkTime())
	                    .overtime(timeDto.getOvertime())
	                    .corrections(correctionDtos)
	                    .userName(targetEmployee.getName())
	                    .department(deptName)
	                    .userPosition(posName)
	                    .workplaceName(workplaceName)
	                    .build();
	    	}
    	}
	    // 사용자 내 근태 현황
	    return AttendanceDetailResponseDto.builder()
	            .status(status)
	            .checkIn(checkIn)
	            .checkOut(checkOut)
	            .totalWorkTime(timeDto.getTotalWorkTime())
	            .basicWorkTime(timeDto.getBasicWorkTime())
	            .overtime(timeDto.getOvertime())
	            .corrections(correctionDtos)
	            .workplaceName(workplaceName)
	            .build();
	}

   // CorrectionDto 변환 로직
	private AttendanceCorrectionRecordDto convertToCorrectionDto(AttendanceCorrection c) {
	    User processor = c.getProcessedBy();
	    Document document = c.getDocument();

	    return AttendanceCorrectionRecordDto.builder()
	            .correctionType(c.getCorrectionType())
	            .processStatus(document != null ? convertStatusToLabel(document.getStatus()) : null)
	            .requestedAt(document != null ? document.getRequestedAt() : null)
	            .beforeTime(c.getBeforeTime())
	            .afterTime(c.getAfterTime())
	            .managerTeam(processor != null && processor.getDepartment() != null 
	             ? processor.getDepartment().getDepartmentName() : "미정")
	            .managerPosition(processor != null && processor.getPosition() != null 
	             ? processor.getPosition().getPositionName() : "미정")
	            .correctionReason(c.getCorrectionReason())
	            .documentId(document != null ? document.getDocumentId() : null)
	            .documentTitle(document != null ? document.getDocumentTitle() : null)
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
	public List<DailyWorkerDto> getDailyWorkerList() {
	    return userRepository.findAll().stream()
	        .filter(u -> u.getEmploymentType() == EmploymentType.DAILY)
	        .map(u -> DailyWorkerDto.builder()
	            .employeeId(u.getEmployeeId())
	            .name(u.getName())
	            .employeeNo(u.getEmployeeNo())
	            .build())
	        .collect(Collectors.toList());
	}
	
	// 일용직 근태 기록 일괄 저장
	@Transactional
	public String saveDailyAttendanceLogs(List<DailyWorkerAttendanceRequestDto> attendanceList) {
		LocalDateTime todayDateTime = getCurrentTime(); // 오늘 날짜, 시간 구하기
		LocalDate todayDate = todayDateTime.toLocalDate();
		
	    // 모든 ID 추출
		List<Long> empIds = attendanceList.stream()
		        .filter(req -> req.getEmployeeId() != null && !req.getEmployeeId().isEmpty())
		        .map(req -> Long.valueOf(req.getEmployeeId()))
		        .collect(Collectors.toList());
	    List<User> foundUsers = userRepository.findAllById(empIds);
	    
	    List<AttendanceLogDaily> existingLogs = attendanceLogDailyRepository.findByEmployeeIdAndWorkDate(empIds, todayDate);
	    
	    Set<Long> existingEmpIds = existingLogs.stream()
	    		.map(log -> log.getEmployee().getEmployeeId())
	    		.collect(Collectors.toSet());
	    
	    // 신규 등록 데이터 필터링
	    List<AttendanceLogDaily> newLogs = new ArrayList<>();
	    
	    // DAILY인 모든 사원 Map
	    Map<Long, User> userMap = foundUsers.stream()
	        .filter(user -> user.getEmploymentType() == EmploymentType.DAILY)
	        .collect(Collectors.toMap(User::getEmployeeId, user -> user));
	    
	    // workplace 필요한 코드 추출
	    Set<WorkplaceCode> workplaceCodes = attendanceList.stream()
    		.map(req -> WorkplaceCode.valueOf(req.getWorkplaceCode()))
	        .collect(Collectors.toSet());

	    // 조회+TEMP로 시작하는 것만 가져오기
	    Map<WorkplaceCode, Workplace> workplaceMap = workplaceRepository
    	    .findByWorkplaceCodeIn(workplaceCodes)
    	    .stream()
    	    .collect(Collectors.toMap(Workplace::getWorkplaceCode, w -> w));

	    for (DailyWorkerAttendanceRequestDto req : attendanceList) {
	    	if (req.getEmployeeId() == null || req.getEmployeeId().trim().isEmpty()) {
	            log.warn(">>> 유효하지 않은 EmployeeId 감지 건너뜁니다.");
	            continue;
	        }
	    	
	    	Long empId = Long.valueOf(req.getEmployeeId());
	    	
	    	if(existingEmpIds.contains(empId)) {
	            log.warn(">>> 이미 데이터가 존재하여 건너뜁니다. ID {}", empId);
	            continue; 
	        }
	    	
	    	User user = userMap.get(empId);
	    	Workplace workplace = workplaceMap.get(WorkplaceCode.valueOf(req.getWorkplaceCode()));
	    	
	        if (user != null && workplace != null) {
	            AttendanceLogDaily log = AttendanceLogDaily.builder()
	                .employee(user)
	                .workplace(workplace)
	                .checkInTime(req.getCheckInDateTime())
	                .checkOutTime(req.getCheckOutDateTime())
	                .workDate(todayDate)
	                .isAttended("Y")
	                .createdAt(todayDateTime)
	                .updatedAt(todayDateTime)
	                .build();
	            newLogs.add(log);
	    	}else {
	    	    log.error(">>> 저장 실패: User({}) 또는 Workplace({})를 찾을 수 없음", empId, req.getWorkplaceCode());
	    	}
	    }
	    
	    attendanceLogDailyRepository.saveAll(newLogs);
	    return String.format("총 %d건 처리 완료 ", attendanceList.size());
	}
	
	// 시간 검증(오후 11시~오전 6시 출퇴근 막기)
	private void validateOperatingTime() {
	    LocalTime todayDate = getCurrentTime().toLocalTime();
	    LocalTime startTime = LocalTime.of(6, 0);
	    LocalTime endTime = LocalTime.of(23, 0);

	    if (todayDate.isBefore(startTime) || todayDate.isAfter(endTime)) {
	    	throw new BusinessException(ErrorCode.OUT_OF_OPERATING_HOURS);
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
		Workplace wp = workplaceRepository.findByWorkplaceCode(WorkplaceCode.HQ)
	            .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
		
		double distance = LocationUtils(latitude, longitude, wp.getLatitude(), wp.getLongitude());

	    // 허용 반경(radius_meter) 이내인지 확인
	    if (distance <= wp.getRadiusMeter()) {
	        return wp;
	    }
	    throw new BusinessException(ErrorCode.INVALID_LOCATION);
	}
	
	// 출근 버튼(직원ID, 위도, 경도)
	public void checkIn(String loginId, Double latitude, Double longitude) {
	    validateOperatingTime(); // 시간 검증
	    LocalDateTime currentTime = getCurrentTime();
	    LocalDate today = currentTime.toLocalDate();

	    User user = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

	    // 위치+거리 검증
	    Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);

	    // 중복 체크
	    AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, today)
	              .orElseThrow(() -> new BusinessException(ErrorCode.BATCH_NOT_RUN));

	    if(result.getAttendanceStatus() != AttendanceStatus.READY) {
	        throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
	    }
	    
	    // 공휴일 체크
	    boolean isHoliday = holidayRepository.findByHolidayDate(today).isPresent();
	    
	    if(isHoliday) {
	    	result.setAttendanceStatus(AttendanceStatus.WORK);
	    	result.setIsHolidayWork("Y");
	    }else {
	    	// 출근 시간 판정 및 저장
	    	AttendanceStatus status = AttendanceStatus.valueOf(attendanceStatusCalculator.determineCheckInStatus(user, currentTime));
	    	result.setAttendanceStatus(status);
	    }

	    result.setCheckInTime(currentTime);
	    result.setUpdatedAt(currentTime);
		result.setIsFixed("N");
	    result.setWorkplace(matchedWorkplace);

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
	            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	
	    AttendanceResult result = attendanceResultRepository.findByEmployeeAndWorkDate(user, today)
	            .orElseThrow(() -> new BusinessException(ErrorCode.CHECK_IN_RECORD_NOT_FOUND));
	
	    // 상태 검증
	    AttendanceStatus status = result.getAttendanceStatus();
	
	    if (status == AttendanceStatus.READY) {
	    	throw new BusinessException(ErrorCode.CHECK_IN_REQUIRED);
	    }
	
	    if (result.getCheckOutTime() != null) {
	        throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
	    }
	
	    // 근무 상태가 아니거나 지각이 아닌 경우(반차 제외)
	    if (status != AttendanceStatus.WORK && status != AttendanceStatus.LATE && status != AttendanceStatus.LEAVE) {
	    	throw new BusinessException(ErrorCode.INVALID_STATUS_FOR_CHECKOUT);
	    }
	
	    // 반차 확인 로직(status가 LEAVE인 경우)
	    if (status == AttendanceStatus.LEAVE) {
	    	//반차 여부 검증
	        boolean isHalfLeave = !leaveDateRepository.findHalfLeaveByUserAndDate(user, today).isEmpty();
	        
	        // 반차가 아니면 퇴근 불가
	        if (!isHalfLeave) {
	        	throw new BusinessException(ErrorCode.LEAVE_RESTRICTION);
	        }
	    }
	
	    // 위치+거리 검증
	    Workplace matchedWorkplace = validateAndGetWorkplace(latitude, longitude);
	
	    // 공휴일 체크
	    boolean isHoliday = holidayRepository.findByHolidayDate(today).isPresent();
	    
	    // 퇴근 처리
	    if(isHoliday) {
	    	result.setAttendanceStatus(AttendanceStatus.OUT);
	    }else {
	    	String resultStatus = attendanceStatusCalculator.determineCheckoutStatus(user, currentTime);
	    	result.setAttendanceStatus(AttendanceStatus.valueOf(resultStatus));
		    
	    }
	    LocalDateTime checkIn = result.getCheckInTime();
	    result.setCheckOutTime(currentTime);    
	    result.setUpdatedAt(currentTime);
	    
	    AttendanceDetailResponseDto times = calculateAttendanceTimes(checkIn, currentTime, isHoliday);
	    result.setTotalWorkMinutes(times.getTotalWorkTime());
	    result.setActualWorkMinutes(times.getBasicWorkTime());
	    result.setOvertimeMinutes(times.getOvertime());
	    
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
	private MonthlyAttendanceSummaryResponseDto createGeneralResponse(List<AttendanceResult> monthList) {
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
        MonthlyAttendanceSummaryResponseDto response = new MonthlyAttendanceSummaryResponseDto();
        
        // work+out 둘 다 출근 처리
        Long workCount = counts.getOrDefault(AttendanceStatus.WORK.name(), 0L);
        Long outCount = counts.getOrDefault(AttendanceStatus.OUT.name(), 0L);
        response.setWorkCount((int)(workCount + outCount));
        
        response.setLateCount(counts.getOrDefault(AttendanceStatus.LATE.name(), 0L).intValue());
        response.setEarlyLeaveCount(counts.getOrDefault(AttendanceStatus.EARLY_LEAVE.name(), 0L).intValue());
        response.setAbsentCount(counts.getOrDefault(AttendanceStatus.ABSENT.name(), 0L).intValue());
        response.setLeaveCount(counts.getOrDefault(AttendanceStatus.LEAVE.name(), 0L).intValue());
        response.setAttendanceList(dtoList); // 전체 목록

        return response;
    }
	
	// 일용직 1명의 월별 통계 - 근태 상태 횟수 체크
	public MonthlyAttendanceSummaryResponseDto createDailyWorkerResponse(List<AttendanceLogDaily> logList) {
		// 카운트 계산
		long workCount = logList.stream()
	            .filter(log -> "Y".equals(log.getIsAttended()))
	            .count();
		
        // DTO 리스트 변환
		List<DailyAttendanceDetailResponseDto> dtoList = logList.stream()
		        .map(log -> DailyAttendanceDetailResponseDto.builder()
		            .workplaceName(log.getWorkplace() != null ? log.getWorkplace().getWorkplaceName() : "미지정")
		            .build())
		        .collect(Collectors.toList());
        
        // 응답 객체 생성 및 반환
		MonthlyAttendanceSummaryResponseDto response = new MonthlyAttendanceSummaryResponseDto();
	    response.setWorkCount((int) workCount);
	    response.setAttendanceList(dtoList);
		
		return response;
	}
	
	// 1명의 월별 통계 - 일반 사용자/관리자
	public MonthlyAttendanceSummaryResponseDto getMonthlyAttendanceStats(String loginId, YearMonth yearMonth, Long targetEmployeeId, boolean isAdmin) {
		// 요청자 정보 조회
		User requester = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		// 조회할 대상 ID 결정(관리자+target 넘어왔으면 해당 직원 조회, 아닐 시 본인 조회)
		Long targetId = (isAdmin && targetEmployeeId != null) ? targetEmployeeId : requester.getEmployeeId();
		
		// 실제 조회할 유저 조회
		User targetUser = userRepository.findById(targetId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	
		// 날짜 범위 설정(1일~말일)
		LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
	    LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		// 일용직 조회
	    if (targetUser.getEmploymentType() == EmploymentType.DAILY) {
	        List<AttendanceLogDaily> dailyLogs = attendanceLogDailyRepository.findAllWithEmployeeByWorkDateBetween(
	                monthStart.toLocalDate(), monthEnd.toLocalDate())
	                .stream()
	                .filter(log -> log.getEmployee().getEmployeeId().equals(targetId))
	                .collect(Collectors.toList());
	        
	        return createDailyWorkerResponse(dailyLogs);
	    }
	    List<AttendanceResult> monthList = attendanceResultRepository.findByEmployeeWithUser(
	            targetUser, 
	            monthStart.toLocalDate(), 
	            monthEnd.toLocalDate()
	    );
	    return createGeneralResponse(monthList);
	}
	
	// active 모든 직원 monthly 통계
	@Transactional(readOnly = true)
	public AdminMonthlyAttendanceListResponseDto getMonthlyAttendanceSummary(YearMonth yearMonth) {
	    LocalDate start = yearMonth.atDay(1);
	    LocalDate end = yearMonth.atEndOfMonth();

	    // 전체 Active 직원 조회
	    List<User> activeUsers = userRepository.findAll().stream()
	            .filter(u -> UserStatus.ACTIVE.equals(u.getStatus()))
	            .toList();

	    // 정규직/일용직 조회 및 map 변환
	    Map<Long, List<AttendanceResult>> regularMap = attendanceResultRepository
	            .findAllWithEmployeeByWorkDateBetween(start, end)
	            .stream()
	            .collect(Collectors.groupingBy(r -> r.getEmployee().getEmployeeId()));
	    Map<Long, List<AttendanceLogDaily>> dailyMap = attendanceLogDailyRepository
	            .findAllWithEmployeeByWorkDateBetween(start, end)
	            .stream()
	            .collect(Collectors.groupingBy(log -> log.getEmployee().getEmployeeId()));
	    
	    // DTO 빌드
	    List<AdminMonthlyAttendanceListResponseDto.EmployeeStats> statsList = activeUsers.stream()
	            .map(user -> {
	                if (user.getEmploymentType() == EmploymentType.DAILY) {
	                    // 일용직 계산(Work count만 집계)
	                    long workCount = dailyMap.getOrDefault(user.getEmployeeId(), Collections.emptyList()).stream()
	                            .filter(l -> "Y".equals(l.getIsAttended())).count();
	                    return buildStats(user, (int)workCount, 0, 0, 0);
	                } else {
	                    // 정규직 계산(기존 로직 재활용)
	                    List<AttendanceResult> results = regularMap.getOrDefault(user.getEmployeeId(), Collections.emptyList());
	                    Map<AttendanceStatus, Long> counts = results.stream()
	                            .collect(Collectors.groupingBy(r -> r.getAttendanceStatus() != null ? r.getAttendanceStatus() : AttendanceStatus.READY, Collectors.counting()));
	                    
	                    return buildStats(user, 
	                            (int)(counts.getOrDefault(AttendanceStatus.WORK, 0L) + counts.getOrDefault(AttendanceStatus.OUT, 0L)),
	                            counts.getOrDefault(AttendanceStatus.LATE, 0L).intValue(),
	                            counts.getOrDefault(AttendanceStatus.ABSENT, 0L).intValue(),
	                            counts.getOrDefault(AttendanceStatus.LEAVE, 0L).intValue());
	                }
	            })
	            .collect(Collectors.toList());

	    return AdminMonthlyAttendanceListResponseDto.builder()
	            .yearMonth(yearMonth)
	            .totalActiveEmployees(activeUsers.size())
	            .employeeStatsList(statsList)
	            .build();
	}

	// 빌더 코드 중복 방지용 헬퍼 메서드
	private AdminMonthlyAttendanceListResponseDto.EmployeeStats buildStats(User user, int work, int late, int absent, int leave) {
	    return AdminMonthlyAttendanceListResponseDto.EmployeeStats.builder()
	            .employeeId(user.getEmployeeId())
	            .name(user.getName())
	            .departmentName(user.getDepartment() != null ? user.getDepartment().getDepartmentName() : "미정")
	            .workCount(work)
	            .lateCount(late)
	            .absentCount(absent)
	            .leaveCount(leave)
	            .build();
	}
	
	// active 모든 직원 day 통계
	@Transactional(readOnly = true)
	public DailyAttendanceSummaryResponseDto getDailyAttendanceSummary(LocalDate date) {
	    // 정규직 근태 결과 조회
	    List<AttendanceResult> regularResults = attendanceResultRepository.findAllByWorkDate(date);
	    
	    // 일용직 ID 리스트 조회
	    List<Long> dailyEmployeeIds = hrEmployeeQueryService.findEmployees(null, UserStatus.ACTIVE, EmploymentType.DAILY, null)
	            .stream()
	            .map(EmployeeListResponseDto::getEmployeeId)
	            .collect(Collectors.toList());
	            
	    // ID 리스트 사용해 일용직 근태 기록 조회
	    List<AttendanceLogDaily> dailyLogs = !dailyEmployeeIds.isEmpty() 
	            ? attendanceLogDailyRepository.findByEmployeeIdAndWorkDate(dailyEmployeeIds, date)
	            : Collections.emptyList();

	    // 정규직 카운트 
	    Map<AttendanceStatus, Long> regularCounts = regularResults.stream()
	            .collect(Collectors.groupingBy(
	                    AttendanceResult::getAttendanceStatus,
	                    Collectors.counting()
	            ));
	    
	    // 정규직 미퇴근 카운트
	    long regularMissingCount = regularResults.stream()
	            .filter(result -> "Y".equals(result.getIsCheckoutMissing()))
	            .count();
	    
	    // 일용직 카운트
	    long dailyWorkCount = dailyLogs.stream().filter(log -> "Y".equals(log.getIsAttended())).count();
	    long dailyReadyCount = dailyEmployeeIds.size() - dailyWorkCount; // 전체 일용직 중 출근 안 한 인원
	    
	 // 통합 DTO 빌드
	    return DailyAttendanceSummaryResponseDto.builder()
	            .regular(AttendanceSummaryCountDto.builder()
	                    .work(regularCounts.getOrDefault(AttendanceStatus.WORK, 0L) + regularCounts.getOrDefault(AttendanceStatus.OUT, 0L))
	                    .late(regularCounts.getOrDefault(AttendanceStatus.LATE, 0L))
	                    .absent(regularCounts.getOrDefault(AttendanceStatus.ABSENT, 0L))
	                    .leave(regularCounts.getOrDefault(AttendanceStatus.LEAVE, 0L))
	                    .missing(regularMissingCount)
	                    .ready(regularCounts.getOrDefault(AttendanceStatus.READY, 0L))
	                    .build())
	            .daily(AttendanceSummaryCountDto.builder()
	                    .work(dailyWorkCount)
	                    .ready(dailyReadyCount)
	                    .missing(0L)
	                    .build())
	            .build();
	}
	
	// 근태 상태와 달력 뱃지 연결용
	public List<CalendarBadgeDto> getMonthlyCalendar(Long employeeId, YearMonth yearMonth) {
        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String formattedMonth = yearMonth.toString();

        if (user.getEmploymentType() == EmploymentType.DAILY) {
            List<AttendanceLogDaily> dailyLogs = attendanceLogDailyRepository.findByEmployeeIdAndMonth(employeeId, formattedMonth);
            log.info(">>> 일용직 캘린더 데이터 조회 (EmployeeId: {}, Month: {}), {}건", employeeId, formattedMonth, dailyLogs.size());
            return dailyLogs.stream()
                .map(log -> CalendarBadgeDto.builder()
                    .date(log.getWorkDate().toString())
                    .status("Y".equals(log.getIsAttended()) ? AttendanceStatus.WORK.name() : null)
                    .checkoutMissing(false)
                    .build())
                .filter(dto -> dto.getStatus() != null) // 출근한 날만 뱃지 표시
                .collect(Collectors.toList());
        } else {
            // 정규직 직원의 경우 attendance_results 테이블에서 조회
            List<AttendanceResult> results = attendanceResultRepository.findByEmployeeIdAndMonth(employeeId, formattedMonth);
            log.info(">>> 정규직 캘린더 데이터 조회 (EmployeeId: {}, Month: {}), {}건", employeeId, formattedMonth, results.size());
            return results.stream()
                .map(result -> CalendarBadgeDto.builder()
                    .date(result.getWorkDate().toString())
                    .status(result.getAttendanceStatus().name())
                    .checkoutMissing("Y".equals(result.getIsCheckoutMissing()))
                    .build())
                .collect(Collectors.toList());
        }
    }
	
	// 일용직 직원 목록 및 근태 로그 조회
	@Transactional(readOnly = true)
	public List<DailyWorkerAttendanceManageDto> getDailyManagementList(LocalDate date) {
	    List<User> employees = userRepository.findAll().stream()
	            .filter(u -> EmploymentType.DAILY.equals(u.getEmploymentType()))
	            .collect(Collectors.toList()); 
	    
	    List<AttendanceLogDaily> logs = attendanceLogDailyRepository.findByWorkDate(date);
	    
	    Map<Long, AttendanceLogDaily> logMap = logs.stream()
	            .collect(Collectors.toMap(
	                log -> log.getEmployee().getEmployeeId(), 
	                log -> log,
	                (existing, replacement) -> existing // 중복 키 발생 시 기존 것 유지
	            ));
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	    return employees.stream().map((User emp) -> {
	        AttendanceLogDaily log = logMap.get(emp.getEmployeeId());
	        return DailyWorkerAttendanceManageDto.builder()
	                .employeeId(emp.getEmployeeId())
	                .name(emp.getName())
	                .logId(log != null ? log.getAttendanceLogsDailyId() : null)
	                .workplaceCode(log != null && log.getWorkplace() != null ? log.getWorkplace().getWorkplaceCode().name() : "")
	                .startTime(log != null && log.getCheckInTime() != null ? log.getCheckInTime().format(formatter) : "")
	                .endTime(log != null && log.getCheckOutTime() != null ? log.getCheckOutTime().format(formatter) : "")
	                .build();
	    }).collect(Collectors.toList());
	}
}