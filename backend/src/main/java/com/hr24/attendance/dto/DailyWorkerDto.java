package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// 일용직 입력 화면에 띄울 직원 목록(일용직 명단 조회)
public class DailyWorkerDto {
    private Long employeeId;   
    private String name;
    private String employeeNo;
}