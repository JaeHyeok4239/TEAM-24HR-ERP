package com.hr24.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
// 일별 근태 상세 조회 - 일용직용(근무지만 추가)
public class DailyAttendanceDetailResponseDto extends AttendanceDetailResponseDto{
	private String workplaceName;
}