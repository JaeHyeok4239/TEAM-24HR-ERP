package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 일용직 직원 목록+근태 로그 합쳐 반환
public class DailyWorkerAttendanceManageDto {
    private Long employeeId;
    private String name;
    private Long logId;
    private String workplaceCode;
    private String startTime;
    private String endTime;
}