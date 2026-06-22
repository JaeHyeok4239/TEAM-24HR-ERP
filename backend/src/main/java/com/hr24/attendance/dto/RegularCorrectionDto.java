package com.hr24.attendance.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
// 사용자가 입력하는 정보(요청용)
public class RegularCorrectionDto {
	private Long resultId; // 수정 대상 ID
    private LocalDateTime newCheckIn; // 수정된 출근 시간
    private LocalDateTime newCheckOut; // 수정된 퇴근 시간
    private String reason; // 수정 사유(기존 비고란)
    
    private String correctionType;
    private LocalDateTime afterTime;
}