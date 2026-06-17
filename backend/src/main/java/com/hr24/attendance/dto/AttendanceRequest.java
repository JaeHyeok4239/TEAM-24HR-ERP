package com.hr24.attendance.dto;

import com.hr24.attendance.entity.AttendanceResult;

import lombok.Getter;

@Getter
public class AttendanceRequest {

    private Long employeeId;
    private Double latitude;
    private Double longitude;

}