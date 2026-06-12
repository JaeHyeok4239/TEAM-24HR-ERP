package com.hr24.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e
    ) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.from(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        
		e.printStackTrace();
		
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.from(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(
	        AccessDeniedException e
	) {
	    ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
	    ErrorResponse response = ErrorResponse.from(errorCode);

	    return ResponseEntity
	            .status(errorCode.getStatus())
	            .body(response);
	}
	
	
}
