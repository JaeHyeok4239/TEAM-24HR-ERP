package com.hr24.document.service;

import org.springframework.stereotype.Service;

import com.hr24.document.entity.DocumentType;
import com.hr24.document.repository.DocumentTypeRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DocumentTypeService{
	
	private final DocumentTypeRepository documentTypeRepository;
	
	public DocumentType createType(DocumentType documentType) {
		
		DocumentType saved = documentTypeRepository.save(documentType);
		
		return saved;
		
	}
}
