package com.hr24.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청")
public class LoginRequestDto {
	
	@NotBlank(message = "로그인 아이디는 필수입니다.")
	@Schema(description = "로그인 아이디", example = "ceo")
	private String loginId;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Schema(description = "비밀번호", example = "1234")
	private String password;
}
