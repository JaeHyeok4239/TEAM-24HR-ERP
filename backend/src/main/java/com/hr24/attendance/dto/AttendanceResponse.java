package com.hr24.attendance.dto;

import java.time.LocalDateTime;

public class AttendanceResponse {

    private LocalDateTime checkInTime;
    private String message;

    public AttendanceResponse(LocalDateTime checkInTime, String message) {
        this.checkInTime = checkInTime;
        this.message = message;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public String getMessage() {
        return message;
    }
}