package com.hr24.document.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.document.dto.DocumentTypeDto;
import com.hr24.document.service.DocumentTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctype")
@Tag(name = "문서 API", description = "문서 유형 조회용 REST API")
public class DocumentTypeController {
	
	private final DocumentTypeService documentTypeService;
	
	//타입 목록
		@GetMapping
	    public ResponseEntity<List<DocumentTypeDto.TypeListResponse>> getTypeList() {
	        return ResponseEntity.ok(documentTypeService.getTypeList());
	    }
		
		// 타입별 스키마 조회
		@GetMapping("/{typeId}/schema")
		public ResponseEntity<DocumentTypeDto.SchemaResponse> getSchema(
		        @PathVariable("typeId") Long typeId) {
		    try {
		        return ResponseEntity.ok(documentTypeService.getSchema(typeId));
		    } catch (EntityNotFoundException e) {
		        // 스키마가 없는 경우 -> 404 (에러 로그 없이 조용히 처리)
		        return ResponseEntity.notFound().build();
		    }
		}
	    
	    
}
