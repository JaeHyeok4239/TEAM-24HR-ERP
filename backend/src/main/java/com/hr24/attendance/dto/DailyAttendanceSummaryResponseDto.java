package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 특정 날짜의 정규직/일용직 근태 상태별 카운트
public class DailyAttendanceSummaryResponseDto{
    private AttendanceSummaryCountDto regular;
    private AttendanceSummaryCountDto daily;
}