package com.hr24.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
// 출퇴근 Dto
public class CheckInOutRequestDto {
    private Double latitude;
    private Double longitude;
    
    @Schema(description = "근무지 코드", allowableValues = {"HQ", "TEMP01", "TEMP02"})
    private String workplaceCode;
}