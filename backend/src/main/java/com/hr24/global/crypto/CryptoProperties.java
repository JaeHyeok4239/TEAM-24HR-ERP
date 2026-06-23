package com.hr24.global.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "hr24.crypto")
public class CryptoProperties {

	@NotBlank(message = "민감정보 암호화 키는 필수입니다.")
	private String secretKey;
}