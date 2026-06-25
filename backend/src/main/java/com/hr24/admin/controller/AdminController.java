package com.hr24.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.service.DocumentManagementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//관리자 기능 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "관리자 API", description = "관리자용 REST API")
public class AdminController {

	private final DocumentManagementService documentManagementService;

	//문서 종류 생성
	//추가 - 스키마 등록
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/document/type")
	ResponseEntity<DocumentType> createDocumentType(DocumentType documentType){
		
		DocumentType result = documentManagementService.createDocumentType(documentType);
		
		return ResponseEntity.ok(result);
	}
	
	//휴가 종류 생성(연차, 반차 등..)
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/document/type/leave")
	ResponseEntity<LeaveType> createLeaveType(LeaveType leaveType) {

		LeaveType result = documentManagementService.createLeaveType(leaveType);

		return ResponseEntity.ok(result);

	}
	
	//임시 저장 제외 모든 문서 조회
	

}
