package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL) // null은 응답에서 제외
// 일별 근태 상세 조회 - 공통 응답 DTO
public class AttendanceDetailResponseDto {
	private AttendanceStatus status; // 상태 코드
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime checkIn; 
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime checkOut;
	
    private Long totalWorkTime; // 총 출근 시간(휴게 포함)
    private Long basicWorkTime; // 기본 근무 시간(휴게, 초과 제외)
    private Long overtime; // 초과 근무 시간
    
    // 관리자 조회 시 필요
    @Builder.Default
    private List<CorrectionDto> corrections = new ArrayList<>();
    private String userName;
    private String department;
    private String userPosition;
}