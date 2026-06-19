package com.hr24.approval.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.approval.dto.ApprovalResponseDto;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalHistoryRepository;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.document.entity.Document;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

//결재 관련 service 모음
@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalLineRepository approvalLineRepository;
	private final UserRepository userRepository;
	private final ApprovalHistoryRepository approvalHistoryRepository;
	private final DocumentRepository documentRepository;
	
	//결재선 조회
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
	
	// 결재 대기함
	public Page<ApprovalResponseDto.ApprovalHistoryDto> PendingApprovalList(String loginId, Pageable pageable){
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
		
		Long userId = user.getEmployeeId();
		
		return approvalHistoryRepository.findPendingApprovals(userId, pageable).map(ApprovalResponseDto.ApprovalHistoryDto::from);
	}
	
	//결재 이력 보관함(PND는 제외)
	public Page<ApprovalResponseDto.ApprovalHistoryDto> listOrSearchApprovalList(String loginId, Long documentType, String status, String keyword, Pageable pageable){
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
		
		Long userId = user.getEmployeeId();
		
		return approvalHistoryRepository.findApprovalList(userId, status, documentType, keyword, pageable).map(ApprovalResponseDto.ApprovalHistoryDto::from);
	}
	
	//결재 승인
	@Transactional
	public void approveDocument(String loginId, Long documentId, String comment) {

	    User approver = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
	    
	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 없습니다"));
	    
	    Long approverId = approver.getEmployeeId();
	    
	    //문서가 요청 상태일때만 승인 처리가 가능하도록
	    if (!"REQ".equals(document.getStatus())) {
	        throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
	    }

	    ApprovalHistory currentHistory = approvalHistoryRepository
	            .findCurrentStepApproval(documentId, approverId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_YOUR_TURN));
	    
	    //본인 담당 결재 이력 승인 처리
	    currentHistory.setStatus("APR");
	    currentHistory.setApproverComment(comment);
	    currentHistory.setActedAt(LocalDateTime.now());
	    
	    
	    Integer maxStep = approvalHistoryRepository.findMaxStepOrder(documentId);
	    
	    if (maxStep == null) {
	        throw new BusinessException(ErrorCode.DATA_NOT_FOUND); // 결재선 데이터 누락
	    }
	    
	    if (document.getCurrentStep().equals(maxStep)) {
	        document.setStatus("APR");                 // 결재 완료 -> PRC는 요청 진행 중, COM은 처리 완료
	    } else {
	        document.setCurrentStep(document.getCurrentStep() + 1);
	    }
	
	}
	
	@Transactional
	public void rejectDocument(String loginId, Long documentId, String comment) {
		
		if(comment == null || comment.isBlank()) {
			throw new IllegalArgumentException("반려 사유를 입력해주세요.");
		}
		
		User approver = userRepository.findByLoginId(loginId)
	            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 없습니다"));

	    //문서 상태 검증(REQ 상태에만 반려 가능)
	    if (!"REQ".equals(document.getStatus())) {
	        throw new BusinessException(ErrorCode.ALREADY_PROCESSED);
	    }

	    Long approverId = approver.getEmployeeId();

	    ApprovalHistory currentHistory = approvalHistoryRepository
	            .findCurrentStepApproval(documentId, approverId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_YOUR_TURN));

	    currentHistory.setStatus("REJ");
	    currentHistory.setApproverComment(comment);
	    currentHistory.setActedAt(LocalDateTime.now());

	    document.setStatus("REJ");
	    document.setRejectReason(comment);
		
	    Integer currentStep = currentHistory.getStepOrder();
	    
	    //결재 다음 단계가 있는 경우에 처리
	    approvalHistoryRepository.cancelFutureSteps(documentId, currentStep);
	   
	}
	
	}
