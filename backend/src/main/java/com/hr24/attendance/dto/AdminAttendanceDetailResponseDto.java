package com.hr24.attendance.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter 
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
// 일별 근태 상세 조회 - 관리자 페이지 추가
public class AdminAttendanceDetailResponseDto extends RegularAttendanceDetailResponseDto {
    private String userName;
    private String department;
    private String userPosition;
    private String workplaceName;
}