package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.YearMonth;
import java.util.List;

@Getter
@Builder
// active 전체 직원 월별 근태 조회
public class MonthlyAttendanceListResponseDto {
    private YearMonth yearMonth;
    private int totalActiveEmployees;
    private List<EmployeeStats> employeeStatsList; // 아래 정의한 inner class 사용

    // 1인 월별 근태 조회
    @Getter
    @Builder
    public static class EmployeeStats {
        private Long employeeId;
        private String name;
        private String departmentName;
        private int workCount;
        private int lateCount;
        private int absentCount;
        private int leaveCount;
        private String employmentType;
    }
}