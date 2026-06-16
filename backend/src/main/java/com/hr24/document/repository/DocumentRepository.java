package com.hr24.document.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.Document;




public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query("select d from Document d where d.requester.employeeId = :currentId and d.status != 'TMP' order by d.createdAt desc")
	Page<Document> myDocList(@Param("currentId") Long currentId, Pageable pageable);
	
    @Query("select d from Document d where d.requester.employeeId = :currentId and d.status = 'TMP' order by d.createdAt desc")
    Page<Document> myTmpDocList(@Param("currentId") Long currentId, Pageable pageable);

}