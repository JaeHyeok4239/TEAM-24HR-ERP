package com.hr24.attendance.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 특정 직원 1명의 월간 근태 카운트 및 목록 응답
public class MonthlyAttendanceSummaryResponseDto {

	// (출근/지각/조퇴/결근/휴가) 선언 및 초기화
	private int workCount = 0;
	private int lateCount = 0;
	private int earlyLeaveCount = 0;
	private int absentCount = 0;
	private int leaveCount = 0;
	private List<? extends AttendanceDetailResponseDto> attendanceList;
	
}