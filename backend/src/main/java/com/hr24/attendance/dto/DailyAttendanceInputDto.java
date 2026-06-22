package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyAttendanceInputDto {
    private Long employeeId;   
    private String name;
    private String employeeNo;
}