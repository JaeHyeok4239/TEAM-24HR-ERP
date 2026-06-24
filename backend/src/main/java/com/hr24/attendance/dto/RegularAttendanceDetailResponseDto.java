package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
// 일별 근태 상세 조회 - 정규직/일반 사용자용(정정 이력 추가)
public class RegularAttendanceDetailResponseDto extends AttendanceDetailResponseDto {
	@Builder.Default
    private List<CorrectionResponseDto> corrections = new ArrayList<>();
}