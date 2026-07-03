package com.hr24.employee.dto.hr;

import java.util.List;

import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeFormOptionsResponseDto {

    private List<DepartmentOptionDto> departments;
    private List<PositionOptionDto> positions;

    @Getter
    @AllArgsConstructor
    public static class DepartmentOptionDto {

        private Long departmentId;
        private String departmentName;

        public static DepartmentOptionDto from(Department department) {
            return new DepartmentOptionDto(
                    department.getDepartmentId(),
                    department.getDepartmentName()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PositionOptionDto {

        private Long positionId;
        private String positionName;

        public static PositionOptionDto from(Position position) {
            return new PositionOptionDto(
                    position.getPositionId(),
                    position.getPositionName()
            );
        }
    }
}