package com.hr24.approval.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.approval.dto.ApprovalManagementDto;
import com.hr24.approval.entity.ApprovalDelegate;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalDelegateRepository;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalManagementService {

	private final ApprovalLineRepository approvalLineRepository;
	private final ApprovalDelegateRepository approvalDelegateRepository;
	private final DocumentTypeRepository documentTypeRepository;
	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;

	// 결재선 생성 - 관리자 및 인사 관리자만 가능
	@Transactional
	public ApprovalLine createApprovalLine(ApprovalManagementDto.ApprovalLineRequestDto requestDto) {
		DocumentType documentType = documentTypeRepository.findById(requestDto.getDocumentType())
				.orElseThrow(() -> new EntityNotFoundException("문서 유형을 찾을 수 없습니다."));

		User approver = userRepository.findById(requestDto.getDefaultApprover())
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

		Department department = null;

		if (requestDto.getDepartmentId() != null) {
			department = departmentRepository.findById(requestDto.getDepartmentId())
					.orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));
		}

		// 중복 확인 (같은 문서유형 + 부서 + 순서)
		if (approvalLineRepository.existsByDocumentTypeAndDepartmentAndStepOrder(documentType, department,
				requestDto.getStepOrder())) {
			throw new IllegalStateException("이미 동일한 결재선이 존재합니다.");
		}

		ApprovalLine approvalLine = ApprovalLine.builder().documentType(documentType)
				.stepOrder(requestDto.getStepOrder()).department(department).approver(approver).build();

		approvalLineRepository.save(approvalLine);
		log.info("결재선 생성 완료 - documentType: {}, stepOrder: {}", documentType.getTypeId(), requestDto.getStepOrder());

		return approvalLine;
	}

	// 대리결재 생성
	@Transactional
	public ApprovalDelegate createApprovalDelegate(ApprovalManagementDto.ApprovalDelegateRequestDto
			requestDto,
			boolean isAdmin, String loginId) {

		User currentUser = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

		User approver = userRepository.findById(requestDto.getApproverId())
				.orElseThrow(() -> new EntityNotFoundException("결재자를 찾을 수 없습니다."));

		User delegate = userRepository.findById(requestDto.getDelegateId())
				.orElseThrow(() -> new EntityNotFoundException("대리결재자를 찾을 수 없습니다."));

		ApprovalLine approvalLine = approvalLineRepository.findById(requestDto.getApprovalLineId())
				.orElseThrow(() -> new EntityNotFoundException("결재선을 찾을 수 없습니다."));

		boolean isOwner = currentUser.getEmployeeId().equals(approver.getEmployeeId());

		// 로그인한 사람이 approverId 본인인지 확인
		if (!isAdmin && !isOwner) {
			throw new AccessDeniedException("본인의 대리결재만 설정할 수 있습니다.");
		}

		// 해당 결재선의 결재자인지 확인
		if (!isAdmin && !approvalLine.getApprover().getEmployeeId().equals(requestDto.getApproverId())) {
			throw new AccessDeniedException("본인이 포함된 결재선만 대리결재를 설정할 수 있습니다.");
		}

		// 중복 확인 - 같은 결재자 + 결재선 + 기간 겹치는 활성 건
		if (approvalDelegateRepository.existsOverlappingDelegate(requestDto.getApproverId(), requestDto.getApprovalLineId(),
				requestDto.getStartDate(), requestDto.getEndDate())) {
			throw new IllegalStateException("해당 기간에 이미 활성화된 대리결재가 존재합니다.");
		}

		ApprovalDelegate approvalDelegate = ApprovalDelegate.builder().approver(approver).delegate(delegate)
				.approvalLine(approvalLine).reason(requestDto.getReason()).startDate(requestDto.getStartDate())
				.endDate(requestDto.getEndDate()).isActive("Y").build();

		approvalDelegateRepository.save(approvalDelegate);
		log.info("대리결재 생성 완료 - approverId: {}, delegateId: {}", requestDto.getApproverId(), requestDto.getDelegateId());

		return approvalDelegate;
	}

	// 대리결재 비활성화
	@Transactional
	public void deactivateDelegate(Long delegateId, String loginId, boolean isAdmin) {
		ApprovalDelegate delegate = approvalDelegateRepository.findById(delegateId)
				.orElseThrow(() -> new EntityNotFoundException("대리결재 설정을 찾을 수 없습니다."));
		
		//delegate에서 가져온 승인자 정보
		User approver = delegate.getApprover();
				
		boolean isOwner = approver.getLoginId().equals(loginId);

		// 로그인한 사람이 approverId 본인인지 확인
		if (!isAdmin && !isOwner) {
			throw new AccessDeniedException("본인의 대리결재만 비활성화 처리할 수 있습니다.");
		}
		
		if(delegate.getIsActive().equals("N")) {
			throw new IllegalStateException("이미 비활성화된 대리결재 입니다");
		}
		
		delegate.setIsActive("N");
		log.info("대리결재 비활성화 완료 - delegateId: {}", delegateId);
	}
}