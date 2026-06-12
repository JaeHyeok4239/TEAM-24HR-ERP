package com.hr24.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// 공통
	INTERNAL_SERVER_ERROR(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"서버 내부 오류가 발생했습니다."),
	
	INVALID_REQUEST(
			HttpStatus.BAD_REQUEST,
			"잘못된 요청입니다."),
	
	// 인증
	USER_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"존재하지 않는 사용자입니다."),
	
	INVALID_PASSWORD(
			HttpStatus.UNAUTHORIZED,
			"아이디 또는 비밀번호가 올바르지 않습니다."),
	
	ACCESS_DENIED(
			HttpStatus.FORBIDDEN,
			"접근 권한이 없습니다."),
	
	INVALID_TOKEN(
			HttpStatus.UNAUTHORIZED,
			"유효하지 않은 토큰입니다.");
			
	
	
	
	private final HttpStatus status;
	private final String message;
	
	ErrorCode(HttpStatus status, String message) {
	    this.status = status;
	    this.message = message;
	}

}
