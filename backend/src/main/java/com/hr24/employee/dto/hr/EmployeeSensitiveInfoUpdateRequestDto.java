package com.hr24.employee.dto.hr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeSensitiveInfoUpdateRequestDto {

	@NotBlank(message = "은행명은 필수입니다.")
	private String bankName;

	@NotBlank(message = "계좌번호는 필수입니다.")
	@Pattern(
			regexp = "^[0-9-]+$",
			message = "계좌번호는 숫자와 하이픈만 입력할 수 있습니다."
	)
	private String accountNumber;

	@NotBlank(message = "예금주는 필수입니다.")
	private String accountHolder;

	@NotBlank(message = "주민등록번호는 필수입니다.")
	@Pattern(
			regexp = "^\\d{6}-?\\d{7}$",
			message = "주민등록번호 형식이 올바르지 않습니다."
	)
	private String rrn;
}