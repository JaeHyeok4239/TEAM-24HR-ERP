package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hr24.attendance.entity.AttendanceResult;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceResponse {

	// (출근/지각/조퇴/결근/휴가) 선언 및 초기화
	private int workCount = 0;
	private int lateCount = 0;
	private int earlyLeaveCount = 0;
	private int absentCount = 0;
	private int leaveCount = 0;
	private List<? extends AttendanceDetailResponseDto> attendanceList;
	
}