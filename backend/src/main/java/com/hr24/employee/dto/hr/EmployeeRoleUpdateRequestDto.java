package com.hr24.employee.dto.hr;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeRoleUpdateRequestDto {

	@NotEmpty(message = "권한은 하나 이상 선택해야 합니다.")
	private List<@NotBlank(message = "권한 코드는 비어 있을 수 없습니다.") String> roleCodes;

	@NotBlank(message = "변경 사유는 필수입니다.")
	private String changeReason;
}