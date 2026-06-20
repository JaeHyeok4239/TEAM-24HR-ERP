package com.hr24.attendance.controller;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "근태 관리 API", description = "근태 관리 관련 API")
public class AttendanceController {
	private final AttendanceService attendanceService;
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