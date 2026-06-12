package com.hr24.document.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hr24.document.dto.DocumentDto;
import com.hr24.document.repository.DocumentAttachMappingRepository;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.global.attachment.service.AttachmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository documentRepository;
	private final DocumentAttachMappingRepository docMappingRepository;
	private final AttachmentService attachmentService;
	
	// 문서 작성
	

	// 문서 수정
//	@Override
//	public void modify(DocumentDto documentDto) {
//	}

	//내 문서함 조회(기본)
	@Override
	public Page<DocumentDto> myDocList(Long currentId, Pageable pageable) {
		return documentRepository.myDocList(currentId, pageable);
	}

	// 임시 저장함 조회(기본)
	@Override
	public Page<DocumentDto> myTmpDocList(Long currentId, Pageable pageable) {
		return documentRepository.myTmpDocList(currentId, pageable);
	}

	// 문서 상세 조회
	@Override
	public DocumentDto view(Long documentId) {
	    return documentRepository.findById(documentId)
	            .map(DocumentDto::from) // 👈 static 메서드를 매핑 메서드로 직접 참조
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다. ID: " + documentId));
	}

	// 문서 삭제(임시 저장 상태일때만)
	// 추후 구현(파일이 있을 경우 파일 매핑 삭제 -> 파일 DB 데이터 삭제 -> 실제 파일 삭제 Transactional)

	// 업로드된 파일 종류 목록
//	@Override
//	public List<AttachmentDto> fileListView(Long documentId) {
//	}
}
