package com.hr24.approval.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.service.ApprovalService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//결재 승인, 결재선, 대리결재 관리
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
@Tag(name = "결재 API", description = "결재 관리용 REST API")
public class ApprovalController {

	private final ApprovalService approvalService;

	// 결재선 조회
	@GetMapping("/line")
	public ResponseEntity<List<ApprovalResponseDto.ApprovalLineDto>> lineList(
			@RequestParam(required = false, value = "document_type") Long documentType, @RequestParam(required = false, value = "keyword") String keyword) {

		return ResponseEntity.ok(approvalService.searchApprovalLines(documentType, keyword));
	}

}
