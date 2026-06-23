package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hr24.attendance.enums.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
// 일별 근태 상세 조회 - 공통 응답 DTO
public class AttendanceDetailResponseDto {
	private AttendanceStatus status; // 상태 코드
    private LocalDateTime checkIn; 
    private LocalDateTime checkOut;
    private Long totalWorkTime; // 총 출근 시간(휴게 포함)
    private Long basicWorkTime; // 기본 근무 시간(휴게, 초과 제외)
    private Long overtime; // 초과 근무 시간
}