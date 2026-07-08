package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 달력 뱃지용
public class CalendarBadgeDto {
    private String date; // yyyy-MM-dd
    private String status; // 근태 상태
    private boolean checkoutMissing; // JSON 응답에 맞춰 필드명 변경 및 boolean 타입으로 설정
}