package com.hr24.document.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.document.dto.DocumentRequestDto;
import com.hr24.document.dto.DocumentResponseDto;
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
	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
	
	// 문서 수정 (임시저장 수정 or 기안)
	@PutMapping(value = "/{documentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> update(
	        @PathVariable("documentId") Long documentId,
	        @io.swagger.v3.oas.annotations.parameters.RequestBody(
	                content = @io.swagger.v3.oas.annotations.media.Content(
	                    encoding = @io.swagger.v3.oas.annotations.media.Encoding(
	                        name = "document",
	                        contentType = MediaType.APPLICATION_JSON_VALUE
	                    )
	                )
	            )
	        @RequestPart("document") DocumentRequestDto.DocumentDto documentDto,
	        @RequestPart(value = "files", required = false) List<MultipartFile> files,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }

	    documentService.updateDocument(documentId, documentDto, files, authInfo.getName());

	    return ResponseEntity.ok().build();
	}

	// 문서 삭제 (임시저장만)
	@DeleteMapping("/{documentId}")
	public ResponseEntity<Void> delete(
	        @PathVariable("documentId") Long documentId,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }

	    documentService.deleteDocument(documentId, authInfo.getName());

	    return ResponseEntity.ok().build();
	}
	
	// 내 문서함
	@GetMapping
	public ResponseEntity<Page<DocumentResponseDto.DocumentListDto>> myDocList(
	        @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }
	    

	    return ResponseEntity.ok(documentService.myDocList(authInfo.getName(), pageable));
	}

	// 임시저장함
	@GetMapping("/tmp")
	public ResponseEntity<Page<DocumentResponseDto.DocumentListDto>> myTmpDocList(
	        @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }

	    return ResponseEntity.ok(documentService.myTmpDocList(authInfo.getName(), pageable));
	}

	// 문서 상세
	@GetMapping("/{documentId}")
	public ResponseEntity<DocumentResponseDto.DocumentDto> view(
	        @PathVariable("documentId") Long documentId,
	        Authentication authInfo) {

	    if (authInfo == null) {
	        throw new BusinessException(ErrorCode.ACCESS_DENIED);
	    }

	    return ResponseEntity.ok(documentService.viewDocument(documentId, authInfo.getName()));
	}
	
	//결재함
	
	//업무 처리함
}
