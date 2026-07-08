package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 일용직 근태 기록 저장 유지용
public class DailyAttendanceManageDto {
    private Long employeeId;
    private String name;
    private Long logId;
    private String workplaceCode;
    private String startTime;
    private String endTime;
}