package com.hr24.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentUpdateRequestDto {

    @NotBlank(message = "부서명을 입력해주세요.")
    @Size(max = 100, message = "부서명은 100자 이하여야 합니다.")
    private String departmentName;

    private Long parentDepartmentId;

    @NotNull(message = "사용 여부를 선택해주세요.")
    private Boolean active;
}