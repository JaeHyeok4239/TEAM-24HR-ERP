package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 정정 이력
public class CorrectionDto {
	private String correctionType; // 정정 종류 IN/OUT
    private String processStatus; // 승인완료/반려/대기
    private LocalDateTime requestedAt; // 신청 일시
    private LocalTime beforeTime; // 수정 전 시간
    private LocalTime afterTime; // 수정 후 시간
    private String managerName; // 담당자(관리자)
    private String remarks; // 비고(최대 100자)
}