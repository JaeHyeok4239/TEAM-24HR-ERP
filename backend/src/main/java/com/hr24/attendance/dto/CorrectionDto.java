package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 정정 이력 조회용(추후 빌더로 값 설정)
public class CorrectionDto {
		private String correctionType; // 정정 종류 IN/OUT
	    private String processStatus; // 승인완료/반려/대기
	    private LocalDateTime requestedAt; // 신청 일시
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	    private LocalDateTime beforeTime; // 수정 전 시간
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	    private LocalDateTime afterTime; // 수정 후 시간
	    private String correctionReason; // 수정 사유(최대 100자)
	    private Long documentId;
	    private String documentTitle; //연결 문서 확인용
	    // 담당자
	    private String managerTeam;
	    private String managerPosition;

}