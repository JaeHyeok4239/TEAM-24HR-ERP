package com.hr24.document.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.dto.DocumentDto;
import com.hr24.document.entity.Document;



public interface DocumentRepository extends JpaRepository<Document, Long>{
	//내 문서함(최신순 정렬)
	@Query("select d from Document d where d.requesterId = :currentUserId order by d.createdAt desc")
	Page<DocumentDto> myDocList(@Param("currentUserId") Long currentUserId, Pageable pageable);
	
	//임시 저장함(최신순 정렬)
	@Query("select d from Document d where d.requesterId = :currentUserId and d.status = 'TMP' order by d.createdAt desc")
	Page<DocumentDto> myTmpDocList(@Param("currentUserId") Long currentUserId, Pageable pageable);

	
}
