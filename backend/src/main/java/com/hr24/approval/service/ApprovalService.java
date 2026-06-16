package com.hr24.approval.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalLineRepository;

import lombok.RequiredArgsConstructor;

//결재 관련 service 모음
@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalLineRepository approvalLineRepository;
	
	public List<ApprovalResponseDto.ApprovalLineDto> searchApprovalLines(Long documentType, String keyword) {
	    List<ApprovalLine> lines = approvalLineRepository.search(documentType, keyword);

	    return lines.stream()
	            .map(ApprovalResponseDto.ApprovalLineDto::from)
	            .toList();
	}
	
	//결재함(내가 결재해야하는 문서 목록)
	
	//결재 처리(최종 승인자라면 문서 상태 PRC로 변경 가능)
}
