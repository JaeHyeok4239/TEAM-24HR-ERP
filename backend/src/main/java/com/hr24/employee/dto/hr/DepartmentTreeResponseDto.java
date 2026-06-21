package com.hr24.employee.dto.hr;

import java.util.List;

import com.hr24.employee.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentTreeResponseDto {

    // 정규직 ACTIVE 전체 인원 수
    private long totalRegularActiveEmployeeCount;

    // 일용직 ACTIVE 전체 인원 수
    private long totalDailyActiveEmployeeCount;

    private List<DepartmentTreeNodeDto> departments;

    @Getter
    @AllArgsConstructor
    public static class DepartmentTreeNodeDto {

        private Long departmentId;
        private String departmentName;

        // 해당 부서에 직접 소속된 정규직 ACTIVE 인원 수
        private long activeEmployeeCount;

        private List<DepartmentTreeNodeDto> children;

        public static DepartmentTreeNodeDto of(
                Department department,
                long activeEmployeeCount,
                List<DepartmentTreeNodeDto> children
        ) {
            return new DepartmentTreeNodeDto(
                    department.getDepartmentId(),
                    department.getDepartmentName(),
                    activeEmployeeCount,
                    children
            );
        }
    }
}