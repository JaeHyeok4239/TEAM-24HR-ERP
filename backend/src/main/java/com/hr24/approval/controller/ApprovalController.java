package com.hr24.approval.controller;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.approval.dto.ApprovalRequestDto;
import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.service.ApprovalService;
import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//결재 승인, 결재선, 대리결재 관리
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
@Tag(name = "결재 API", description = "결재 관리용 REST API")
public class ApprovalController {

	private final ApprovalService approvalService;
	
	// 결재선 조회(추후 페이지 적용)
	@GetMapping("/line")
	public ResponseEntity<Page<ApprovalResponseDto.ApprovalLineDto>> lineList(
	        @RequestParam(required = false, value = "keyword") String keyword,
	        @PageableDefault(size = 10, sort = "approvalLineId", direction = Sort.Direction.DESC) Pageable pageable) {

	    return ResponseEntity.ok(approvalService.listOrSearchApprovalLines(keyword, pageable));
	}
	

	// 결재 이력 보관함
	@GetMapping("/history")
	public ResponseEntity<Page<ApprovalResponseDto.ApprovalHistoryDto>> approvalList(
			@RequestParam(required = false, value = "document_type") Long documentType,
			@RequestParam(required = false, value = "status") String status, 
			@RequestParam(required = false, value = "keyword") String keyword,
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
	        Authentication authInfo) {
		
		if(authInfo == null) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
		
		String loginId = authInfo.getName();
		
		return ResponseEntity.ok(approvalService.listOrSearchApprovalList(loginId, documentType, status, keyword, pageable));
				
	}
	
	// 결재 대기함
	@GetMapping("/")
	public ResponseEntity<Page<ApprovalResponseDto.ApprovalHistoryDto>> pendingList(
			@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			Authentication authInfo) {

		if (authInfo == null) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		String loginId = authInfo.getName();

		return ResponseEntity.ok(approvalService.PendingApprovalList(loginId, pageable));
	}
	
	//더티 체킹을 통한 결재 승인 처리
	@PostMapping("/{documentId}/approve")
	public ResponseEntity<Void> process(
	        @PathVariable("documentId") Long documentId,
	        @RequestBody ApprovalRequestDto.ApprovalProcessDto request,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }
	    
	    String loginId = authInfo.getName();
	    
	    switch (request.getAction()) {
	    case APR -> approvalService.approveDocument(loginId, documentId, request.getComment());
	    case REJ -> approvalService.rejectDocument(loginId, documentId, request.getComment());
	    }

	    return ResponseEntity.ok().build();
	}
	

}
