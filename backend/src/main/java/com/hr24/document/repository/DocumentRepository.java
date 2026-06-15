package com.hr24.document.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.document.entity.Document;



public interface DocumentRepository extends JpaRepository<Document, Long>{
	//내 문서함(최신순 정렬)
	@Query("select d from Document d where d.requester = :currentId order by d.createdAt desc")
	Page<DocumentResponseDto> myDocList(@Param("currentId") Long currentId, Pageable pageable);
	
	//임시 저장함(최신순 정렬)
	@Query("select d from Document d where d.requester = :currentId and d.status = 'TMP' order by d.createdAt desc")
	Page<DocumentResponseDto> myTmpDocList(@Param("currentId") Long currentUserId, Pageable pageable);
	
}
