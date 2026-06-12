package com.hr24.document.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.auth.jwt.JwtProvider;
import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.document.service.DocumentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
@Tag(name = "문서 API", description = "문서 관리용 REST API")
public class DocumentController {

	private final DocumentService documentService;
	private final JwtProvider jwtProvider;
	
//회원 기능 완성 시 구현(로그인 id로 currentId(employeeId) 받아와서 documentList 반환)
//	@GetMapping("/myDocs")
//	public ResponseEntity<Page<DocumentDto>> getMyDocList(String loginId, Pageable pageable){
//		
//		
//		
//		
//		Page<DocumentDto> result = documentService.myDocList(currentId, pageable);
//		
//		return ResponseEntity.ok(result);
//	}
//	
//	@GetMapping("/myTmp")
//	public ResponseEntity<Page<DocumentDto>> getMyTmpDocList(String loginId, Pageable pageable){
//		Page<DocumentDto> result = documentService.myTmpDocList(currentId, pageable);
//		
//		return ResponseEntity.ok(result);
//	}
	
	@GetMapping("/{documentId}")
	public ResponseEntity<DocumentResponseDto> getView(@PathVariable("documentId") Long documentId){
		return ResponseEntity.ok().body(documentService.view(documentId));
	}
}
