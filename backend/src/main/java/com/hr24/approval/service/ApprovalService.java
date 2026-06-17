package com.hr24.approval.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalHistoryRepository;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

//결재 관련 service 모음
@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalLineRepository approvalLineRepository;
	private final UserRepository userRepository;
	private final ApprovalHistoryRepository approvalHistoryRepository;
	
	public List<ApprovalResponseDto.ApprovalLineDto> listOrSearchApprovalLines(Long departmentId, Long documentType, String keyword) {
	    
		List<ApprovalLine> lines;
		
		if(departmentId == null && documentType == null && keyword == "") {

			lines = approvalLineRepository.findAll();
			System.out.println(lines.size());
		}
		
		else {
			
			lines = approvalLineRepository.search(departmentId, documentType, keyword);
		
		}
		
		
		return lines.stream()
				.map(ApprovalResponseDto.ApprovalLineDto::from)
				.toList();
	}
	
	public List<ApprovalResponseDto.ApprovalLineDto> ApprovalLineList(){
		List<ApprovalLine> lines = approvalLineRepository.findAll(Sort.by(Sort.Direction.DESC, "approvalLineId"));
		
		return lines.stream().map(ApprovalResponseDto.ApprovalLineDto::from).toList();
	}
	
	//결재 처리(최종 승인자라면 문서 상태 PRC로 변경 가능)
	
	// 결재함 조회
	public Page<ApprovalResponseDto.ApprovalHistoryDto> PendingApprovalList(String loginId, Pageable pageable){
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
		
		Long userId = user.getEmployeeId();
		
		return approvalHistoryRepository.findPendingApprovals(userId, pageable).map(ApprovalResponseDto.ApprovalHistoryDto::from);
	}
	
	//결재함 조회(검색 + 필터)
	public Page<ApprovalResponseDto.ApprovalHistoryDto> listOrSearchApprovalList(String loginId, Long documentType, String status, String keyword, Pageable pageable){
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
		
		Long userId = user.getEmployeeId();
		
		return approvalHistoryRepository.findApprovalList(userId, status, documentType, keyword, pageable).map(ApprovalResponseDto.ApprovalHistoryDto::from);
	}
	}
