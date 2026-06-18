package com.hr24.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentCreateRequestDto {

    @NotBlank(message = "부서 코드를 입력해주세요.")
    @Size(max = 50, message = "부서 코드는 50자 이하여야 합니다.")
    @Pattern(
        regexp = "^[A-Z0-9_-]+$",
        message = "부서 코드는 영문 대문자, 숫자, _, -만 사용할 수 있습니다."
    )
    private String departmentCode;

    @NotBlank(message = "부서명을 입력해주세요.")
    @Size(max = 100, message = "부서명은 100자 이하여야 합니다.")
    private String departmentName;

    private Long parentDepartmentId;

    @NotNull(message = "사용 여부를 선택해주세요.")
    private Boolean active = true;
}