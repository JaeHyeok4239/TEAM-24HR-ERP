package com.hr24.document.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hr24.document.dto.DocumentDto;

//문서 작업에 필요한 기능 정의
public interface DocumentService {
	//문서 작성(문서 작성 시 첨부파일 있는 경우 첨부파일도 함께 mapping)
	//Long write(DocumentDto documentDto);
	
	//문서 수정(임시 저장 상태일 때만 가능)
	//void modify(DocumentDto documentDto);
	
	//반려 문서 재기안(반려 상태일 때, 원본 반려 문서 조회 -> 내용 복사 후 임시저장 상태로 저장(기존 매핑되었던 첨부파일 불러온 뒤 최종 확정된 파일들만 재매핑)
	//Long createReDraft(Long rejectedDocumentId)
	
	//내 문서함 조회(기본)
	//Page<DocumentDto> myDocList(Long currentId, Pageable pageable);
	
	//임시 저장함 조회(기본)
	//Page<DocumentDto> myTmpDocList(Long currentId, Pageable pageable);
	
	//문서 상세 조회
	DocumentDto view(Long documentId);
	
	//문서 삭제(임시 저장 상태일 때만 가능 / 문서 삭제 시 매핑 데이터 삭제 -> 파일 데이터 삭제 -> 실제 파일 삭제 Transactional 처리)
	//void delete(Long documentId);
	
	//업로드된 파일 종류 목록
}
