package com.hr24.document.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.approval.repository.ApprovalHistoryRepository;
import com.hr24.document.dto.DocumentRequestDto;
import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.document.dto.HrRequestDto;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.repository.DocumentFileRepository;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.document.repository.DocumentTypeRepository;
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
	private final HrService hrService;
	private final ObjectMapper objectMapper;

	// 파일 매핑
	private void createFileMapping(Document document, Attachment attachment) {
		DocumentFile documentFile = DocumentFile.builder().document(document).attachment(attachment).build();

		documentFileRepository.save(documentFile);
	}

	// 여러 파일 등록
	private void createDocumentFiles(Document document, List<Attachment> attachments) {
		for (Attachment attachment : attachments) {
			createFileMapping(document, attachment);
		}
	}

	// documentContent를 Map으로 변환
	private Map<String, Object> toDocumentContentMap(List<DocumentRequestDto.DocumentContentDto> contentList) {
		if (contentList == null || contentList.isEmpty()) {
			return new HashMap<>();
		}

		return contentList.stream().collect(Collectors.toMap(DocumentRequestDto.DocumentContentDto::getField,
				DocumentRequestDto.DocumentContentDto::getData));
	}

	// 결재 연동
	private void createApprovalHistory(Document document) {
		List<ApprovalLine> lines = approvalLineRepository
				.findByDocumentTypeOrderByStepOrderAsc(document.getDocumentType());

		if (lines.isEmpty()) {
			throw new IllegalStateException("해당 문서 종류의 결재선이 없습니다.");
		}

		List<ApprovalHistory> histories = lines.stream().map(line -> ApprovalHistory.builder().document(document)
				.stepOrder(line.getStepOrder()).approver(line.getApprover()).status("PND").build()).toList();

		approvalHistoryRepository.saveAll(histories);
	}

	private void processDetailTable(Document document, DocumentRequestDto.DocumentDto documentDto, boolean isSubmit) {
		String detailTable = document.getDocumentType().getDetailTable();

		if ("leave".equals(detailTable)) {
			if (isSubmit) {
				hrService.createLeaveFromContent(document, document.getDocumentContent());
			} else {
				// 임시저장 시 형식만 맞춰서 검증 (저장은 안 함, 에러 시 무시)
				try {
					objectMapper.convertValue(document.getDocumentContent(), HrRequestDto.LeaveDto.class);
				} catch (IllegalArgumentException e) {
					// 임시저장이니 무시하거나, 로그만 남김
				}
			}
		}
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

		Map<String, Object> documentContent = toDocumentContentMap(documentDto.getDocumentContent());

		Document document = Document.builder().documentType(documentType).requester(user)
				.documentTitle(documentDto.getDocumentTitle()).documentContent(documentContent).status(status)
				.requestedAt("REQ".equals(status) ? now : null).build();

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

		if ("REQ".equals(document.getStatus())
				&& (documentDto.getDocumentContent() == null || documentDto.getDocumentContent().isEmpty())) {
			throw new IllegalArgumentException("문서 내용을 입력해주세요.");
		}

		// detailTable에 따른 분기별 처리
		processDetailTable(document, documentDto, "REQ".equals(saved.getStatus()));

		return saved.getDocumentId();

	}

	// 수정
	@Transactional
	public void updateDocument(Long documentId, DocumentRequestDto.DocumentDto documentDto, List<MultipartFile> files,
			String loginId) {

		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		if (!"TMP".equals(document.getStatus())) {
			throw new IllegalStateException("임시저장 상태의 문서만 수정할 수 있습니다");
		}

		String newStatus = documentDto.getStatus();
		if (newStatus == null || newStatus.isBlank()) {
			newStatus = "TMP";
		}

		document.setDocumentTitle(documentDto.getDocumentTitle());
		document.setDocumentContent(toDocumentContentMap(documentDto.getDocumentContent()));
		document.setStatus(newStatus);

		// 파일 삭제
		if (documentDto.getDeleteAttachmentIds() != null && !documentDto.getDeleteAttachmentIds().isEmpty()) {
			for (Long attachmentId : documentDto.getDeleteAttachmentIds()) {
				documentFileRepository.deleteByAttachment_AttachmentId(attachmentId);
				attachmentService.delete(attachmentId);
			}
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

		if ("REQ".equals(newStatus)) {
			createApprovalHistory(document);
			document.setRequestedAt(LocalDateTime.now());
		}

		if ("REQ".equals(document.getStatus())
				&& (documentDto.getDocumentContent() == null || documentDto.getDocumentContent().isEmpty())) {
			throw new IllegalArgumentException("문서 내용을 입력해주세요.");
		}

		// detailTable에 따른 분기별 처리
		processDetailTable(document, documentDto, "REQ".equals(document.getStatus()));

	}

	// 문서 삭제(임시 저장 상태일 때만 가능 / 문서 삭제 시 매핑 데이터 삭제 -> 파일 데이터 삭제 -> 실제 파일 삭제
	// Transactional 처리)
	@Transactional
	public void deleteDocument(Long documentId, String loginId) {

		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		if (!"TMP".equals(document.getStatus())) {
			throw new IllegalStateException("임시저장 상태의 문서만 삭제할 수 있습니다");
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

	// 반려 문서 재기안(반려 상태일 때, 원본 반려 문서 조회 -> 내용 복사 후 임시저장 상태로 저장(기존 매핑되었던 첨부파일 불러온 뒤 최종
	// 확정된 파일들만 재매핑)
	// Long createReDraft(Long rejectedDocumentId)

	// 업로드된 파일 종류 목록

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

		Long userId = user.getEmployeeId();

		// 기안자 체크
		boolean isRequester = document.getRequester().getEmployeeId().equals(userId);

		// 결재자 체크 (approval_history에 있는지) 문서 상세 열람은 결재자 권한 있으면 가능
		boolean isApprover = approvalHistoryRepository.existsByDocumentAndApprover(document, user);

		if (!isRequester && !isApprover) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		List<DocumentResponseDto.DocumentApprovalDto> approvalHistories = approvalHistoryRepository
				.findByDocumentOrderByStepOrderAsc(document).stream().map(DocumentResponseDto.DocumentApprovalDto::from)
				.toList();

		List<DocumentResponseDto.DocumentFileResponseDto> documentFileList = documentFileRepository
				.findByDocument(document).stream().map(DocumentResponseDto.DocumentFileResponseDto::from).toList();

		return DocumentResponseDto.DocumentDto.of(document, approvalHistories, documentFileList);
	}

}
