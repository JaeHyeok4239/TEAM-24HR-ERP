package com.hr24.attendance.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.attendance.dto.AttendanceCorrectionRequestDto;
import com.hr24.attendance.dto.AttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.dto.CheckInOutRequestDto;
import com.hr24.attendance.dto.DailyAttendanceInputDto;
import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.enums.AttendanceStatus;
import com.hr24.attendance.service.AttendanceCorrectionService;
import com.hr24.attendance.service.AttendanceProcessService;
import com.hr24.attendance.service.AttendanceService;
import com.hr24.employee.dto.hr.EmployeeListResponseDto;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.service.HrEmployeeQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "근태 관리 API", description = "근태 관리 관련 API")
public class AttendanceController {
	private final AttendanceService attendanceService;
	private final UserRepository userRepository;
	private final AttendanceCorrectionService attendanceCorrectionService;
	private final AttendanceProcessService attendanceProcessService;
	private final HrEmployeeQueryService hrEmployeeQueryService;
	
	@Operation(summary = "오후 배치 프로그램", description = "오후 11시에 실행되는 프로그램입니다. 오후 11시~오전6시 출퇴근 불가 및 미퇴근자 Missing 처리")
    @PostMapping("/batch/closing-batch")
    public ResponseEntity<String> closingBatch() {
        attendanceService.processMissingCheckouts();
        System.out.println(">>> 오후 배치 프로그램이 수동 실행되었습니다.");
        return ResponseEntity.ok("오후 배치 프로그램이 실행되었습니다.");
    }
    
	@Operation(summary = "오전 배치 프로그램", description = "오전 6시에 실행되는 프로그램입니다. 휴가자 제외 active 직원 ready 상태로 result 생성")
    @PostMapping("/batch/open-batch")
    public ResponseEntity<String> openBatch() {
    	attendanceService.createDailyAttendanceResults();
    	System.out.println(">>> 오전 배치 프로그램이 수동 실행되었습니다.");
    	return ResponseEntity.ok("오전 배치 프로그램이 실행되었습니다.");
    }
	
	@Operation(summary = "직원 타입에 따른 직원 목록 조회", description = "정규직/일용직 직원 목록 조회")
	@GetMapping("/employees")
	public List<EmployeeListResponseDto> getEmployees(
	        @Parameter(description = "직원 타입(REGULAR/DAILY)", example = "REGULAR")
	        @RequestParam(value = "type") EmploymentType type,
	        @Parameter(description = "(선택) 부서 고유 번호", example = "1")
	        @RequestParam(value = "departmentId", required = false) Long departmentId,
	        @Parameter(description = "(선택) 이름 또는 사번 검색어", example = "홍길동")
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @Parameter(description = "(선택) 근태 상태", example = "WORK")
	        @RequestParam(value = "status", required = false) AttendanceStatus status,
	        @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
      return attendanceService.findEmployeesWithFilters(type, departmentId, keyword, status, date);
	}
	
    // (관리자)일용직 근태 기록 수정
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTENDANCE')")
    @PatchMapping("/daily/{logId}")
    @Operation(summary = "일용직 근태 기록 수정", description = "관리자가 일용직 근태 기록을 직접 수정합니다. 출퇴근 한번에 수정 가능")
    public ResponseEntity<Void> applyDailyCorrection(
    		@PathVariable("logId") Long logId,
            @RequestBody List<DailyCorrectionDto> dtoList,
            @AuthenticationPrincipal String loginId) {
        
    	attendanceCorrectionService.applyDailyCorrection(logId, dtoList, loginId);
        return ResponseEntity.ok().build();
    }
    
    // (관리자)정규직 근태 기록 정정
    @Operation(summary = "정규직 근태 기록 수정", description = "정규직 근태 기록이 정정됩니다.")
    @PostMapping("/regular/{logId}/correction")
    public ResponseEntity<Void> applyDocumentCorrection(
            @PathVariable("logId") Long logId,
            @RequestBody AttendanceCorrectionRequestDto.CorrectionDto dto,
            @AuthenticationPrincipal String loginId) {
        
    	attendanceCorrectionService.applyRegularCorrection(logId, dto, loginId);
        return ResponseEntity.ok().build();
    }
	
//	 일별 근태 상세 조회
//	 관리자는 모든 사용자 조회 가능
//	 일반 사용자는 본인 것만 조회 가능
    @Operation(summary = "일별 근태 상세 조회", description = "관리자(정규직, 일용직) 혹은 본인 것만 조회 가능")
    @PreAuthorize("isAuthenticated()") // 로그인만 되어 있으면 일단 호출 가능
    @GetMapping("/{employeeId}")
    public ResponseEntity<AttendanceDetailResponseDto> getAttendanceDetail(
    		@Parameter(description = "조회 대상 사번")
            @PathVariable("employeeId") Long employeeId,
            @Parameter(description = "조회하려는 날짜(yyyy-MM-dd)")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        String loginId = authentication.getName();

        return ResponseEntity.ok(attendanceService.getAttendanceDetail(loginId, employeeId, date, isAdmin));
    }
	
	// 일용직 명단 조회
	@GetMapping("/daily-workers")
	public List<DailyAttendanceInputDto> getDailyWorkers() {
	    // userService를 통해 가져오되 본인이 만든 DTO로 변환해서 반환
	    return attendanceService.getDailyWorkerList();
	}
	
	// 일용직 근태 기록 일괄 저장
	@Operation(summary = "일용직 근태 기록 일괄 저장", description = "관리자가 화면에서 입력한 일용직 근태 명단을 저장합니다.")
	@PostMapping("/batch/daily-batch")
	public ResponseEntity<String> dailyBatch(
	        @RequestBody List<AttendanceRequest> attendanceList) { // Map > DTO 리스트로 변경
	    
		String resultMessage = attendanceService.saveDailyAttendanceLogs(attendanceList);
	    return ResponseEntity.ok(resultMessage);
	}
	
	@Operation(summary = "출근", description = "출근할 수 있습니다.")
	@PostMapping("/check-in")
	public ResponseEntity<String> checkIn(Authentication authentication, @RequestBody CheckInOutRequestDto request){
	    // authentication에서 loginId 뽑기
	    String loginId = authentication.getName();
	    
	    // 서비스로 loginId 넘김
	    attendanceService.checkIn(loginId, request.getLatitude(), request.getLongitude());
	    return ResponseEntity.ok("정상적으로 출근 처리되었습니다.");
	}
	
	@Operation(summary = "퇴근", description = "퇴근할 수 있습니다.")
	@PostMapping("/check-out")
	public ResponseEntity<String> checkOut(Authentication authentication, @RequestBody CheckInOutRequestDto request){
		String loginId = authentication.getName();
		attendanceService.checkOut(loginId, request.getLatitude(), request.getLongitude());
		return ResponseEntity.ok("정상적으로 퇴근 처리되었습니다.");
	}
	
	@Operation(summary = "월별 근태 횟수 조회", description = "본인 또는 관리자가 사원의 월별 근태 횟수를 조회합니다. 관리자는 targetEmployeeId를 입력해 조회 가능합니다.")
	@GetMapping("/summary")
	public ResponseEntity<AttendanceResponse> getMonthlyAttendanceStats(
	        Authentication authentication, 
	        @RequestParam(name="yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
	        @RequestParam(name="targetEmployeeId", required = false) Long targetEmployeeId) {
		
		String loginId = authentication.getName(); 

		// 관리자 여부 확인
	    // 본인 프로젝트의 권한 이름이 "ROLE_ADMIN"인지 "ADMIN"인지 확인해서 맞게 수정
	    boolean isAdmin = authentication.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ADMIN"));
	    
	    AttendanceResponse response = attendanceService.getMonthlyAttendanceStats(loginId, yearMonth, targetEmployeeId, isAdmin);
	    
	    return ResponseEntity.ok(response);
	}
	
}