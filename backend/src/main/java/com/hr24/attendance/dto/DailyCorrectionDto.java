package com.hr24.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCorrectionDto {
    private LocalDateTime beforeTime; // 수정 전 시간
	private LocalDateTime afterTime; // 수정할 시간
    private String correctionReason; // 수정 사유(최대 100자)
}