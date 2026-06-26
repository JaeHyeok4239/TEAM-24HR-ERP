package com.hr24.document.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.approval.entity.ApprovalDelegate;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.approval.repository.ApprovalDelegateRepository;
import com.hr24.approval.repository.ApprovalHistoryRepository;
import com.hr24.document.dto.DocumentRequestDto;
import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.repository.DocumentFileRepository;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.LeaveTypeRepository;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.attachment.Attachment;
import com.hr24.global.attachment.service.AttachmentService;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentFileRepository documentFileRepository;
	private final DocumentTypeRepository documentTypeRepository;
	private final ApprovalHistoryRepository approvalHistoryRepository;
	private final ApprovalLineRepository approvalLineRepository;
	private final UserRepository userRepository;
	private final AttachmentService attachmentService;
	private final ApprovalDelegateRepository approvalDelegateRepository;
	private final DocumentContentValidationService documentContentValidationService;
	private final LeaveTypeRepository leaveTypeRepository;
	
	// 파일 매핑
	private void createFileMapping(Document document, Attachment attachment) {
		DocumentFile documentFile = DocumentFile.builder().document(document).attachment(attachment).build();

		documentFileRepository.save(documentFile);
	}

	// 작성자 검증
	private boolean isOwner(String loginId, Long requesterId) {
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

		return user.getEmployeeId().equals(requesterId);
	}

	// 여러 파일 등록
	private void createDocumentFiles(Document document, List<Attachment> attachments) {
		for (Attachment attachment : attachments) {
			createFileMapping(document, attachment);
		}
	}

	// 결재 연동
	private void createApprovalHistory(Document document) {
	    Department department = document.getRequester().getDepartment();
	    
	    // 부서별 결재선 우선 조회
	    List<ApprovalLine> lines = approvalLineRepository
	            .findByDocumentTypeAndDepartmentOrderByStepOrderAsc(
	                    document.getDocumentType(), department);

	    // 없으면 공통 결재선 fallback
	    if (lines.isEmpty()) {
	        lines = approvalLineRepository
	                .findByDocumentTypeAndDepartmentIsNullOrderByStepOrderAsc(
	                        document.getDocumentType());
	    }

	    if (lines.isEmpty()) {
	        throw new IllegalStateException("해당 문서 종류의 결재선이 없습니다.");
	    }

	    LocalDate today = LocalDate.now();

	    List<ApprovalHistory> histories = lines.stream()
	            .map(line -> {
	                // 대리결재 활성화 여부 체크
	                User approver = approvalDelegateRepository
	                        .findActiveDelegate(
	                                line.getApprover().getEmployeeId(),
	                                line.getApprovalLineId(),
	                                today)
	                        .map(ApprovalDelegate::getDelegate)
	                        .orElse(line.getApprover());

	                return ApprovalHistory.builder()
	                        .document(document)
	                        .stepOrder(line.getStepOrder())
	                        .approver(approver)
	                        .status("PND")
	                        .documentVersion(document.getDocumentVersion())
	                        .build();
	            }).toList();

	    approvalHistoryRepository.saveAll(histories);
	}

	// 문서 작성
	@Transactional
	public Long createDocument(DocumentRequestDto.DocumentDto documentDto, List<MultipartFile> files, String loginId) {

		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

		DocumentType documentType = documentTypeRepository.findById(documentDto.getDocumentType())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서 타입입니다"));

		LocalDateTime now = LocalDateTime.now();
		String status = documentDto.getStatus();

		if (status == null || status.isBlank()) {
			status = "TMP";
		}
		
		if ("REQ".equals(documentDto.getStatus())
				&& (documentDto.getDocumentContent() == null || documentDto.getDocumentContent().isEmpty())) {
			throw new IllegalArgumentException("문서 내용을 입력해주세요.");
		}
		
		if ("REQ".equals(status)) {
		    documentContentValidationService.validate(documentType, documentDto.getDocumentContent());
		}
		
		Document document = Document.builder()
				.documentType(documentType).requester(user)
				.documentTitle(documentDto.getDocumentTitle())
				.documentContent(documentDto.getDocumentContent())
				.status(status)
				.requestedAt("REQ".equals(status) ? now : null)
				.documentVersion(1)
				.build();

		Document saved = documentRepository.save(document);

		// 파일 있을 경우
		if (files != null && !files.isEmpty()) {
			List<MultipartFile> uploadFiles = files.stream().filter(file -> !file.isEmpty()).toList();

			Long uploader = saved.getRequester().getEmployeeId();

			if (!uploadFiles.isEmpty()) {
				List<Attachment> attachments = attachmentService.upload(uploadFiles, uploader);

				createDocumentFiles(saved, attachments);
			}
		}

		if ("REQ".equals(saved.getStatus())) {
			// 결재 로직 추가
			createApprovalHistory(document);
		}

		return saved.getDocumentId();

	}

	// 수정
	@Transactional
	public void updateDocument(Long documentId, DocumentRequestDto.DocumentDto documentDto, List<MultipartFile> files,
			String loginId) {

		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));
		
		boolean isOwner = isOwner(loginId, document.getRequester().getEmployeeId());

		if (!isOwner) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
		
		if (!"TMP".equals(document.getStatus()) && !"REJ".equals(document.getStatus())) {
			throw new IllegalStateException("수정 불가능한 상태입니다.");
		}
		
		String newStatus = documentDto.getStatus();

		if (!"TMP".equals(newStatus) && !"REQ".equals(newStatus)) {
		    throw new IllegalArgumentException("허용되지 않는 상태값입니다.");
		}

		if ("REQ".equals(newStatus)
				&& (documentDto.getDocumentContent() == null || documentDto.getDocumentContent().isEmpty())) {
			throw new IllegalArgumentException("문서 내용을 입력해주세요.");
		}
		
		boolean isResubmit = "REJ".equals(document.getStatus()) && "REQ".equals(newStatus);

		// 반려 재기안 시 처리
		if (isResubmit) {
			document.setRejectReason(null);
			document.setCurrentStep(1);
			document.setUpdatedAt(LocalDateTime.now());
			document.setDocumentVersion(document.getDocumentVersion() + 1);
		}
		
		if ("REQ".equals(newStatus)) {
		    documentContentValidationService.validate(document.getDocumentType(), documentDto.getDocumentContent());
		    createApprovalHistory(document);
		    document.setRequestedAt(LocalDateTime.now());
		}
		
		document.setDocumentTitle(documentDto.getDocumentTitle());
		document.setDocumentContent(documentDto.getDocumentContent());
		document.setStatus(newStatus);

		// 파일 삭제
		if (documentDto.getAttachmentIds() != null && !documentDto.getAttachmentIds().isEmpty()) {
			// 매핑 삭제
			documentFileRepository.deleteByAttachmentIdIn(documentDto.getAttachmentIds());
			
			// 파일 삭제
			attachmentService.deleteAll(documentDto.getAttachmentIds());
		}

		// 파일 추가
		if (files != null && !files.isEmpty()) {
			List<MultipartFile> uploadFiles = files.stream().filter(file -> !file.isEmpty()).toList();
			if (!uploadFiles.isEmpty()) {
				List<Attachment> attachments = attachmentService.upload(uploadFiles,
						document.getRequester().getEmployeeId());
				createDocumentFiles(document, attachments);
			}
		}

	}

	// 문서 삭제(임시 저장 상태일 때만 가능 / 문서 삭제 시 매핑 데이터 삭제 -> 파일 데이터 삭제 -> 실제 파일 삭제
	// Transactional 처리)
	@Transactional
	public void deleteDocument(Long documentId, String loginId) {

		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));
		
		
		boolean isOwner = isOwner(loginId, document.getRequester().getEmployeeId());

		if (!isOwner) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
		if (!"TMP".equals(document.getStatus())) {
			throw new IllegalStateException("임시저장 상태의 문서만 삭제할 수 있습니다");
		}
		
		//이미 결재처리가 된 문서의 경우 삭제 방지(반려 문서 삭제 방지)
		if(!document.getDocumentVersion().equals(1)) {
			throw new IllegalStateException("결재 이력이 존재하는 문서는 삭제할 수 없습니다");
		}
		
		// 매핑된 파일 조회 후 삭제
		List<DocumentFile> documentFiles = documentFileRepository.findByDocument(document);
		for (DocumentFile documentFile : documentFiles) {
			Long attachmentId = documentFile.getAttachment().getAttachmentId();
			documentFileRepository.delete(documentFile);
			attachmentService.delete(attachmentId);
		}

		documentRepository.delete(document);
	}

	// 내 문서함 조회(기본)
	public Page<DocumentResponseDto.DocumentListDto> myDocList(String loginId, Pageable pageable) {

		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

		Long currentId = user.getEmployeeId();

		return documentRepository.myDocList(currentId, pageable).map(DocumentResponseDto.DocumentListDto::from);
	}

	// 임시 저장함 조회(기본)
	public Page<DocumentResponseDto.DocumentListDto> myTmpDocList(String loginId, Pageable pageable) {

		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

		Long currentId = user.getEmployeeId();

		return documentRepository.myTmpDocList(currentId, pageable).map(DocumentResponseDto.DocumentListDto::from);
	}

	// 문서 상세 조회
	public DocumentResponseDto.DocumentDto viewDocument(Long documentId, String loginId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

		
		Long requester = document.getRequester().getEmployeeId();
		
		// 기안자 체크
		boolean isRequester = isOwner(loginId, requester);

		// 결재자 체크 (approval_history에 있는지) 문서 상세 열람은 결재자 권한 있으면 가능
		boolean isApprover = approvalHistoryRepository.existsByDocumentAndApprover(document, user);

		if (!isRequester && !isApprover) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		List<DocumentResponseDto.DocumentApprovalDto> approvalHistories = approvalHistoryRepository
			    .findByDocumentAndDocumentVersionOrderByStepOrderAsc(document, document.getDocumentVersion())
			    .stream()
			    .map(DocumentResponseDto.DocumentApprovalDto::from)
			    .toList();

		List<DocumentResponseDto.DocumentFileResponseDto> documentFileList = documentFileRepository
				.findByDocument(document).stream().map(DocumentResponseDto.DocumentFileResponseDto::from).toList();
		
		return DocumentResponseDto.DocumentDto.of(document, approvalHistories, documentFileList);
	}

}
