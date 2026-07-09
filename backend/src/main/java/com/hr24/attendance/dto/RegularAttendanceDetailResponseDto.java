package com.hr24.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
// 일별 근태 상세 조회 - 정규직/일반 사용자용(정정 이력 추가)
public class RegularAttendanceDetailResponseDto extends AttendanceDetailResponseDto {
}