package com.hr24.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 근무지 목록 응답
public class WorkplaceResponseDto {
    private String name; // 근무지 이름
    private Double latitude; // 위도
    private Double longitude; // 경도
}