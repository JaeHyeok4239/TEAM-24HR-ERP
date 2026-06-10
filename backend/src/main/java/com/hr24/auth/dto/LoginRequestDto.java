package com.hr24.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
	
	private String loginId;
	
	private String password;
}
