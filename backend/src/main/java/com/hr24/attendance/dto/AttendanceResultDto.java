package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hr24.attendance.entity.AttendanceResult;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AttendanceResultDto {
    private Long attendanceResultId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String attendanceStatus;

    // 엔티티를 받아 필요한 값만 뽑아 저장
    public AttendanceResultDto(AttendanceResult result) {
        this.attendanceResultId = result.getAttendanceResultId();
        this.checkInTime = result.getCheckInTime();
        this.checkOutTime = result.getCheckOutTime();
        this.attendanceStatus = result.getAttendanceStatus();
    }
}