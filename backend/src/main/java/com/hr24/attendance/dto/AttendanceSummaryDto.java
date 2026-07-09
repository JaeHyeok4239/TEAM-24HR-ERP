package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// active 모든 직원 일별 통계
public class AttendanceSummaryDto {
    private long work;
    private long late;
    private long absent;
    private long leave;
    private long ready;
    private long missing;
}