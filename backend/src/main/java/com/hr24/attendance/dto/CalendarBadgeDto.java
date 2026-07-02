package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 달력 뱃지용
public class CalendarBadgeDto {
    private String date; // yyyy-MM-dd
    private String status; // 근태 상태
}