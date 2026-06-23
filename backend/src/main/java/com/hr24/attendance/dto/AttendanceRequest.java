package com.hr24.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hr24.attendance.entity.AttendanceResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {

    private String employeeId;
    
    @Schema(description = "근무지 코드", allowableValues = {"HQ", "TEMP01", "TEMP02"})
    private String workplaceCode;

    @Schema(description = "출근시간", example = "09:00")
    private String checkIn;
    
    @Schema(description = "퇴근시간", example = "18:00")
    private String checkOut; 
    
    @JsonIgnore
    public LocalDateTime getCheckInDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkIn));
    }
    
    @JsonIgnore
    public LocalDateTime getCheckOutDateTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkOut));
    }
}

