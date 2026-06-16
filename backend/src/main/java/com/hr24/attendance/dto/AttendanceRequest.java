package com.hr24.attendance.dto;

import lombok.Getter;

@Getter
public class AttendanceRequest {

    private Long employeeId;
    private Double latitude;
    private Double longitude;

}