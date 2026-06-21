package com.hr24.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hr24.attendance.entity.AttendanceResult;

import lombok.Getter;

@Getter
public class AttendanceRequest {

    private String employeeId;
    private Double latitude;
    private Double longitude;
    
    private String workplaceCode;
    private String checkIn;
    private String checkOut; 
    
    // LocalDate와 LocalTime을 합쳐 LocalDateTime으로 변환
    @JsonIgnore
    public LocalDateTime getCheckInDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkIn));
    }
    @JsonIgnore
    public LocalDateTime getCheckOutDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkOut));
    }

}

