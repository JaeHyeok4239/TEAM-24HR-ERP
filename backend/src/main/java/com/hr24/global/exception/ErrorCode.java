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
	
    // 직원 관리
    DUPLICATE_LOGIN_ID(
            HttpStatus.CONFLICT,
            "이미 사용 중인 로그인 ID입니다."
    ),

    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "이미 사용 중인 이메일입니다."
    ),

    DUPLICATE_EMPLOYEE_NO(
            HttpStatus.CONFLICT,
            "이미 사용 중인 사번입니다."
    ),

    INVALID_EMPLOYEE_CREATE_REQUEST(
            HttpStatus.BAD_REQUEST,
            "직원 등록 정보가 올바르지 않습니다."
    ),

    ROLE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "권한 정보를 찾을 수 없습니다."
    ),
	
	//부서(조회 및 수정)
	DEPARTMENT_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"부서를 찾을 수 없습니다." ),
	
	DUPLICATE_DEPARTMENT_CODE(
			HttpStatus.CONFLICT,
			"이미 사용 중인 부서 코드입니다." ),
	
	DUPLICATE_DEPARTMENT_NAME(
			HttpStatus.CONFLICT,
			"이미 사용 중인 부서명입니다." ),
	
	INVALID_PARENT_DEPARTMENT(
			HttpStatus.BAD_REQUEST, 
		"올바르지 않은 상위 부서입니다." ), 

	INACTIVE_PARENT_DEPARTMENT(
			HttpStatus.BAD_REQUEST,
			"미사용 부서는 상위 부서로 지정할 수 없습니다." ),
	
	DEPARTMENT_HAS_ASSIGNED_EMPLOYEES(
			HttpStatus.CONFLICT, 
		"해당 부서에 소속된 직원이 있어 미사용 처리할 수 없습니다."), 

	DEPARTMENT_HAS_ACTIVE_CHILDREN(
			HttpStatus.CONFLICT,
			"사용 중인 하위 부서가 있어 미사용 처리할 수 없습니다." ),
	DATA_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"데이터가 존재하지 않습니다."
			),
	
	// 직급
	POSITION_NOT_FOUND(
	        HttpStatus.NOT_FOUND,
	        "직급을 찾을 수 없습니다."
	),
	DUPLICATE_POSITION_CODE(
	        HttpStatus.CONFLICT,
	        "이미 사용 중인 직급 코드입니다."
	),
	DUPLICATE_POSITION_NAME(
	        HttpStatus.CONFLICT,
	        "이미 사용 중인 직급명입니다."
	),
	POSITION_HAS_ASSIGNED_EMPLOYEES(
	        HttpStatus.CONFLICT,
	        "해당 직급을 사용하는 직원이 있어 미사용 처리할 수 없습니다."
	),
	INVALID_POSITION_ORDER(
	        HttpStatus.BAD_REQUEST,
	        "직급 위치가 올바르지 않습니다."
	),
	
	// 인증
	AUTHENTICATION_REQUIRED(
	        HttpStatus.UNAUTHORIZED,
	        "인증이 필요합니다."),
	
	USER_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"존재하지 않는 사용자입니다."),
	
	INVALID_PASSWORD(
			HttpStatus.UNAUTHORIZED,
			"아이디 또는 비밀번호가 올바르지 않습니다."),
	
	CURRENT_PASSWORD_MISMATCH(
	        HttpStatus.BAD_REQUEST,
	        "현재 비밀번호가 일치하지 않습니다."
	),

	SAME_AS_CURRENT_PASSWORD(
	        HttpStatus.BAD_REQUEST,
	        "새 비밀번호는 현재 비밀번호와 다르게 입력해주세요."
	),
	
	ACCESS_DENIED(
			HttpStatus.FORBIDDEN,
			"접근 권한이 없습니다."),
	
	INVALID_TOKEN(
			HttpStatus.UNAUTHORIZED,
			"유효하지 않은 토큰입니다."),
	
	//결재 관련
	ALREADY_PROCESSED(
			HttpStatus.CONFLICT,
			"이미 처리 중이거나 완료된 요청입니다."),
	
	NOT_YOUR_TURN(
			HttpStatus.BAD_REQUEST,
			"해당 단계의 결재자만 요청이 가능합니다.");

	
	
	private final HttpStatus status;
	private final String message;
	
	ErrorCode(HttpStatus status, String message) {
	    this.status = status;
	    this.message = message;
	}

}
