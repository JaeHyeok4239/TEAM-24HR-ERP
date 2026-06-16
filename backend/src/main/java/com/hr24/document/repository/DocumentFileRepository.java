package com.hr24.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;

public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{
	void deleteByAttachment_AttachmentId(Long attachmentId);
	List<DocumentFile> findByDocument(Document document);
}
