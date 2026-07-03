package com.hr24.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.document.dto.DocumentManagementDto;
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
	
	//문서 종류 생성(detail_table이 있는 경우에는 스키마 추가)
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/document/type")
	public ResponseEntity<Void> createDocumentType(
	        @RequestBody DocumentManagementDto.DocumentTypeRequestDto requestDto) {
	    documentManagementService.createDocumentType(requestDto);
	    return ResponseEntity.ok().build();
	}
	
	//스키마 생성
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/document/schema")
	public ResponseEntity<Void> createDocumentTypeSchema(
	        @RequestBody DocumentManagementDto.DocumentTypeSchemaRequestDto requestDto) {
	    documentManagementService.createDocumentTypeSchema(requestDto);
	    return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/document/schema/{schemaId}")
	public ResponseEntity<Void> updateDocumentTypeSchema(
	        @PathVariable("schemaId") Long schemaId,
	        @RequestBody DocumentManagementDto.DocumentTypeSchemaRequestDto requestDto) {
	    documentManagementService.updateDocumentTypeSchema(schemaId, requestDto);
	    return ResponseEntity.ok().build();
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
