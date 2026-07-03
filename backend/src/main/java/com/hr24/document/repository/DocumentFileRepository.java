package com.hr24.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;

public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{
	void deleteByAttachment_AttachmentId(Long attachmentId);
	List<DocumentFile> findByDocument(Document document);
	@Modifying
	@Query("DELETE FROM DocumentFile df WHERE df.attachment.attachmentId IN :attachmentIds")
	void deleteByAttachmentIdIn(@Param("attachmentIds") List<Long> attachmentIds);
}
