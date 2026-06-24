package com.hr24.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
// 출퇴근 Dto
public class CheckInOutRequestDto {
    private Double latitude;
    private Double longitude;
}