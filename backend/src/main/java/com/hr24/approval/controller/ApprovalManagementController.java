package com.hr24.approval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.approval.dto.ApprovalManagementDto;
import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.entity.ApprovalDelegate;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.service.ApprovalManagementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval/management")
@Tag(name = "결재 관리 API", description = "결재 관리용 REST API")
public class ApprovalManagementController {

	private final ApprovalManagementService approvalManagementService;

	// 결재선은 관리자와 인사 총괄만 생성 가능
	@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
	@PostMapping("/line")
	public ResponseEntity<ApprovalResponseDto.ApprovalLineDto> createApprovalLine(
			@RequestBody ApprovalManagementDto.ApprovalLineRequestDto requestDto) {

		ApprovalLine approvalLine = approvalManagementService.createApprovalLine(requestDto);

		return ResponseEntity.ok(ApprovalResponseDto.ApprovalLineDto.from(approvalLine));
	}

	// 대리 결재 생성은 컨트롤러에서 권한 검증 x(본인이 결재선에 결재자로 들어있으면 됨)
	@PostMapping("/delegate")
	public ResponseEntity<ApprovalResponseDto.ApprovalDelegateDto> createDelegate(
			@RequestBody ApprovalManagementDto.ApprovalDelegateRequestDto requestDto, Authentication authInfo) {

		// 관리자 및 인사 관리자는 통과할 수 있도록 검증 메소드 전달
		boolean isAdmin = authInfo.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("HR_LEAD"));
		
		String loginId = authInfo.getName();
		
		ApprovalDelegate approvalDelegate = approvalManagementService.createApprovalDelegate(requestDto, isAdmin, loginId);

		return ResponseEntity.ok(ApprovalResponseDto.ApprovalDelegateDto.from(approvalDelegate));
	}
	
	@PatchMapping("/delegate/{delegateId}")
	public ResponseEntity<Void> deactiveDelegate(@PathVariable("delegateId") Long delegateId, Authentication authInfo){
		
		boolean isAdmin = authInfo.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("HR_LEAD"));
		
		
		String loginId = authInfo.getName();
		
		approvalManagementService.deactivateDelegate(delegateId, loginId, isAdmin);
		
		return ResponseEntity.ok().build();
	}

}
