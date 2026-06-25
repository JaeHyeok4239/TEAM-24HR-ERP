package com.hr24.document.service;

import org.springframework.stereotype.Service;

import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.DocumentTypeSchemaRepository;
import com.hr24.document.repository.LeaveTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentManagementService {

	private final DocumentTypeRepository documentTypeRepository;
	private final DocumentTypeSchemaRepository documentTypeSchemaRepository;
	private final LeaveTypeRepository leaveTypeRepository;

	public DocumentType createDocumentType(DocumentType documentType) {

		// 검증 로직 추가 + detailTable 있을 경우 스키마 설정

		DocumentType saved = documentTypeRepository.save(documentType);

		return saved;

	}

	public LeaveType createLeaveType(LeaveType leaveType) {

		LeaveType saved = leaveTypeRepository.save(leaveType);

		return saved;
	}
}
