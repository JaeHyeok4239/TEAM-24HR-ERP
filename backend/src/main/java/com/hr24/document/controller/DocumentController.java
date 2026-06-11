package com.hr24.document.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.document.dto.DocumentDto;
import com.hr24.document.service.DocumentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
public class DocumentController {

	private final DocumentService documentService;
	
//	@GetMapping("/myDocs")
//	public ResponseEntity<Page<DocumentDto>> getMyDocList(String loginId, Pageable pageable){
//		
//		
//		
//		Long currentId = 
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
	
	@GetMapping("/view")
	public ResponseEntity<DocumentDto> getView(@RequestParam("documentId") Long documentId){
		return ResponseEntity.ok().body(documentService.view(documentId));
	}
}
