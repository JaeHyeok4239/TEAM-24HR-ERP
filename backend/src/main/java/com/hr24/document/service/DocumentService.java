package com.hr24.document.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.approval.entity.ApprovalHistory;
import com.hr24.approval.entity.ApprovalLine;
import com.hr24.approval.repository.ApprovalLineRepository;
import com.hr24.approval.repository.ApprovalHistoryRepository;
import com.hr24.document.dto.DocumentRequestDto;
import com.hr24.document.dto.DocumentRequestDto.DocumentContentDto;
import com.hr24.document.dto.DocumentResponseDto;
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
			List<ApprovalLine> lines = approvalLineRepository.findByDocumentTypeOrderByStepOrderAsc(documentType);

			if (lines.isEmpty()) {
				throw new IllegalStateException("해당 문서 종류의 결재선이 없습니다.");
			}

			List<ApprovalHistory> histories = lines.stream().map(line -> ApprovalHistory.builder().document(saved)
					.stepOrder(line.getStepOrder()).approver(line.getApprover()).status("PND").build()).toList();

			approvalHistoryRepository.saveAll(histories);
		}

		return saved.getDocumentId();

	}

	// 문서 수정(임시 저장 상태일 때만 가능)
	// void modify(DocumentDto documentDto);

	// 반려 문서 재기안(반려 상태일 때, 원본 반려 문서 조회 -> 내용 복사 후 임시저장 상태로 저장(기존 매핑되었던 첨부파일 불러온 뒤 최종
	// 확정된 파일들만 재매핑)
	// Long createReDraft(Long rejectedDocumentId)

	// 문서 삭제(임시 저장 상태일 때만 가능 / 문서 삭제 시 매핑 데이터 삭제 -> 파일 데이터 삭제 -> 실제 파일 삭제
	// Transactional 처리)
	// void delete(Long documentId);

	// 업로드된 파일 종류 목록

	// 내 문서함 조회(기본)
	public Page<DocumentResponseDto> myDocList(Long currentId, Pageable pageable) {
		return documentRepository.myDocList(currentId, pageable);
	}

	// 임시 저장함 조회(기본)
	public Page<DocumentResponseDto> myTmpDocList(Long currentId, Pageable pageable) {
		return documentRepository.myTmpDocList(currentId, pageable);
	}

	// 문서 상세 조회
//	public DocumentResponseDto.DocumentDto view(Long documentId) {
//		Document document = documentRepository.findById(documentId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서"));
//		
//		List<DocumentResponseDto.DocumentFileDto> documentFileList = document.getDocumentFileList()
//				.stream()
//				.map(DocumentResponseDto.DocumentFileDto::from)
//				.collect(Collectors.toList());
//		
//	}

	// 문서 삭제(임시 저장 상태일때만)
	// 추후 구현(파일이 있을 경우 파일 매핑 삭제 -> 파일 DB 데이터 삭제 -> 실제 파일 삭제 Transactional)

	// 업로드된 파일 종류 목록
//	public List<AttachmentDto> fileListView(Long documentId) {
//	}
}
