package com.hr24.attendance.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.attendance.entity.AttendanceResult;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AttendanceResultDto extends AttendanceDetailResponseDto{
    private Long attendanceResultId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime checkInTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime checkOutTime;
    private String attendanceStatus;

    // 엔티티를 받아 필요한 값만 뽑아 저장
    public AttendanceResultDto(AttendanceResult result) {
        this.attendanceResultId = result.getAttendanceResultId();
        this.checkInTime = result.getCheckInTime();
        this.checkOutTime = result.getCheckOutTime();
        // Enum > String 변환
        if (result.getAttendanceStatus() != null) {
            this.attendanceStatus = result.getAttendanceStatus().name();
        } else {
            this.attendanceStatus = null;
        }
    }
}