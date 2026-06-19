package com.hr24.attendance.controller;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.attendance.dto.AttendanceRequest;
import com.hr24.attendance.dto.AttendanceResponse;
import com.hr24.attendance.service.AttendanceService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "근태 관리 API", description = "근태 관리 관련 API")
public class AttendanceController {
	private final AttendanceService attendanceService;
	
	//마감 배치 프로그램 바구니
    @PostMapping("/batch/closing-batch")
    public String closingBatch() {
        attendanceService.processMissingCheckouts();
        return "마감 배치 프로그램이 실행되었습니다.";
    }
	
	// 출근 바구니
	@PostMapping("/check-in")
	public ResponseEntity<String> checkIn(@RequestBody AttendanceRequest request){
		attendanceService.checkIn(request.getEmployeeId(), request.getLatitude(), request.getLongitude());
		return ResponseEntity.ok("정상적으로 출근 처리되었습니다.");
	}
	
	// 퇴근 바구니
	@PostMapping("/check-out")
	public ResponseEntity<String> checkOut(@RequestBody AttendanceRequest request){
		attendanceService.checkOut(request.getEmployeeId(), request.getLatitude(), request.getLongitude());
		return ResponseEntity.ok("정상적으로 퇴근 처리되었습니다.");
	}
	
	// 달력 바구니
	@GetMapping("/summary")
	public ResponseEntity<AttendanceResponse> getMonthlyAttendance(
			@RequestParam("employeeId") Long employeeId,
			@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth){
		AttendanceResponse response = attendanceService.yearMonth(employeeId, yearMonth);
	    return ResponseEntity.ok(response);
	}
	
	
}