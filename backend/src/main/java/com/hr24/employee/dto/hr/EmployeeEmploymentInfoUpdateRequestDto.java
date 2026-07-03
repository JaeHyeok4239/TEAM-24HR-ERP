package com.hr24.employee.dto.hr;

import java.time.LocalDate;

import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEmploymentInfoUpdateRequestDto {

	private Long departmentId;

	private Long positionId;

	@NotNull(message = "고용형태는 필수입니다.")
	private EmploymentType employmentType;

	@NotNull(message = "재직상태는 필수입니다.")
	private UserStatus status;

	@NotNull(message = "입사일은 필수입니다.")
	private LocalDate hireDate;

	private LocalDate resignationDate;

	@NotBlank(message = "변경 사유는 필수입니다.")
	private String changeReason;
}