package com.hr24.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청")
public class LoginRequestDto {
	
	@Schema(description = "로그인 아이디", example = "ceo")
	private String loginId;
	
	@Schema(description = "비밀번호", example = "1234")
	private String password;
}
