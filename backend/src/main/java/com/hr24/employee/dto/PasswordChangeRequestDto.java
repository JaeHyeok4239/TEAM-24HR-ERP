package com.hr24.employee.dto;

import java.lang.invoke.StringConcatFactory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeRequestDto {

	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	private String currentPassword;
	
	@NotBlank(message = "새 비밀번호를 입력해주세요.")
	@Size(min = 8, max = 20, message = "새 비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
		message = "새 비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	private String newPassword;
}
