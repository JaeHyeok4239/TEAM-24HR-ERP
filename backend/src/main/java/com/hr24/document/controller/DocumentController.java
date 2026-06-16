package com.hr24.document.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.document.dto.DocumentRequestDto;
import com.hr24.document.entity.Document;
import com.hr24.document.service.DocumentService;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/document")
@Tag(name = "문서 API", description = "문서 관리용 REST API")
public class DocumentController {

	private final DocumentService documentService;
	
	//임시 저장 + 결재 요청
	@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Long> write(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
			        content = @io.swagger.v3.oas.annotations.media.Content(
			            encoding = @io.swagger.v3.oas.annotations.media.Encoding(
			                name = "document", 
			                contentType = MediaType.APPLICATION_JSON_VALUE
			            )
			        )
			    )
			@RequestPart("document")DocumentRequestDto.DocumentDto documentDto, 
			@RequestPart(value = "files", required = false) List<MultipartFile> files, 
			Authentication authInfo) {
		
		if(authInfo == null) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
		
		Long documentId = documentService.createDocument(documentDto, files, authInfo.getName());
		
		return ResponseEntity.ok(documentId);
	}
	
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
	
//	@GetMapping("/{documentId}")
//	public ResponseEntity<DocumentResponseDto> getView(@PathVariable("documentId") Long documentId){
//		return ResponseEntity.ok().body(documentService.view(documentId));
//	}
}
