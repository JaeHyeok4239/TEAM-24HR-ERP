package com.hr24.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 일용직 근태 일괄 입력 요청
public class DailyWorkerAttendanceRequestDto {

    private String employeeId;
    
    @Schema(description = "근무지 코드", allowableValues = {"HQ", "TEMP01", "TEMP02"})
    private String workplaceCode;

    @Schema(description = "출근시간", example = "09:00")
    @JsonProperty("checkIn")
    private String checkIn;
    
    @Schema(description = "퇴근시간", example = "18:00")
    @JsonProperty("checkOut")
    private String checkOut; 
    
    @JsonIgnore
    public LocalDateTime getCheckInDateTime() {
    	// 값 없으면 null 리턴
        if (this.checkIn == null || this.checkIn.isEmpty()) {
            return null;
        }
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkIn));
    }
    
    @JsonIgnore
    public LocalDateTime getCheckOutDateTime() {
    	// 값 없으면 null 리턴
        if (this.checkOut == null || this.checkOut.isEmpty()) {
            return null;
        }
        return LocalDateTime.of(LocalDate.now(), LocalTime.parse(this.checkOut));
    }
    
}

