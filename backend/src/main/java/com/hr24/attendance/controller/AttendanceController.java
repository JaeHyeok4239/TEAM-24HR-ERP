package com.hr24.attendance.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.attendance.dto.AttendanceDetailResponseDto;
import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.dto.DailyAttendanceInputDto;
import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.dto.RegularCorrectionDto;
import com.hr24.attendance.service.AttendanceCorrectionService;
import com.hr24.attendance.service.AttendanceService;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "근태 관리 API", description = "근태 관리 관련 API")
public class AttendanceController {
	private final AttendanceService attendanceService;
	private final UserRepository userRepository;
	private final AttendanceCorrectionService attendanceCorrectionService;
	
	@Operation(summary = "오후 배치 프로그램", description = "오후 11시에 실행되는 프로그램입니다.")
    @PostMapping("/batch/closing-batch")
    public ResponseEntity<String> closingBatch() {
        attendanceService.processMissingCheckouts();
        System.out.println(">>> 오후 배치 프로그램이 수동 실행되었습니다.");
        return ResponseEntity.ok("오후 배치 프로그램이 실행되었습니다.");
    }
    
	@Operation(summary = "오전 배치 프로그램", description = "오전 6시에 실행되는 프로그램입니다.")
    @PostMapping("/batch/open-batch")
    public ResponseEntity<String> openBatch() {
    	attendanceService.createDailyAttendanceResults();
    	System.out.println(">>> 오전 배치 프로그램이 수동 실행되었습니다.");
    	return ResponseEntity.ok("오전 배치 프로그램이 실행되었습니다.");
    }
	
	// 정규직 정정
    @PreAuthorize("hasRole('ADMIN') or @attendanceSecurity.isOwner(authentication, #employeeId)")
    @Operation(summary = "정규직 근태 기록 정정", description = "승인된 정정 요청을 바탕으로 관리자가 정규직 근태 기록을 정정합니다.")
    @PatchMapping("/employees/{employeeId}/regular/{resultId}")
    public ResponseEntity<Void> correctRegularAttendance(
            @PathVariable("employeeId") Long employeeId,
            @PathVariable("resultId") Long resultId,
            @RequestParam("documentId") Long documentId,
            @RequestBody RegularCorrectionDto dto) {
        
        // 서비스 호출 시 documentId 전달
    	attendanceCorrectionService.correctRegular(employeeId, resultId, dto, documentId);
        return ResponseEntity.ok().build();
    }

    // 일용직 정정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/daily/{logId}")
    @Operation(summary = "일용직 근태 기록 수정", description = "관리자가 일용직 근태 기록을 직접 수정합니다.")
    public ResponseEntity<Void> correctDailyAttendance(
            @PathVariable Long logId, 
            @RequestBody DailyCorrectionDto dto) {
    	attendanceCorrectionService.correctDaily(logId, dto);
        return ResponseEntity.ok().build();
    }
	
	// 일별 근태 상세 조회
	// 관리자는 모든 사용자 조회 가능
	// 일반 사용자는 본인 것만 조회 가능
    @Operation(summary = "일별 근태 상세 조회", description = "특정 직원의 날짜별 근태 기록을 상세하게 조회합니다.(관리자/본인만 가능)")
    @PreAuthorize("hasRole('ADMIN') or @attendanceSecurity.isOwner(authentication, #employeeId)")
    @GetMapping("/{employeeId}")
    public ResponseEntity<AttendanceDetailResponseDto> getAttendanceDetail(
            @PathVariable("employeeId") Long employeeId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {

        // isAdmin 여부 체크
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
	        @RequestBody List<AttendanceRequest> attendanceList) { // Map -> DTO 리스트로 변경
	    
	    attendanceService.saveDailyAttendanceLogs(attendanceList);
	    return ResponseEntity.ok(attendanceList.size() + "명의 근태 기록이 성공적으로 저장되었습니다.");
	}
	
	@Operation(summary = "출근", description = "출근할 수 있습니다.")
	@PostMapping("/check-in")
	public ResponseEntity<String> checkIn(Authentication authentication, @RequestBody AttendanceRequest request){
	    // authentication에서 loginId 뽑기
	    String loginId = authentication.getName();
	    
	    // 서비스로 loginId 넘김
	    attendanceService.checkIn(loginId, request.getLatitude(), request.getLongitude());
	    return ResponseEntity.ok("정상적으로 출근 처리되었습니다.");
	}
	
	@Operation(summary = "퇴근", description = "퇴근할 수 있습니다.")
	@PostMapping("/check-out")
	public ResponseEntity<String> checkOut(Authentication authentication, @RequestBody AttendanceRequest request){
		String loginId = authentication.getName();
		attendanceService.checkOut(loginId, request.getLatitude(), request.getLongitude());
		return ResponseEntity.ok("정상적으로 퇴근 처리되었습니다.");
	}
	
	@Operation(summary = "개인 월별 근태 횟수 조회", description = "yyyy-mm 형태로 넣으면 해당 달의 근태 횟수를 조회할 수 있습니다.\n(출근/지각/조퇴/결근/휴가)")
	@GetMapping("/summary")
	public ResponseEntity<AttendanceResponse> getMonthlyAttendanceStats(
	        Authentication authentication, 
	        @RequestParam(name="yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
		
	    String loginId = authentication.getName(); 
	    AttendanceResponse response = attendanceService.yearMonth(loginId, yearMonth);
	    return ResponseEntity.ok(response);
	}
	
}