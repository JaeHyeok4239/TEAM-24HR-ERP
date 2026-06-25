package com.hr24.document.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.Document;
import com.hr24.employee.entity.Department;




public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query("select d from Document d where d.requester.employeeId = :currentId and d.status != 'TMP'")
	Page<Document> myDocList(@Param("currentId") Long currentId, Pageable pageable);
	
    @Query("select d from Document d where d.requester.employeeId = :currentId and d.status = 'TMP'")
    Page<Document> myTmpDocList(@Param("currentId") Long currentId, Pageable pageable);

    //documentType으로 문서 찾기(결재 완료된 문서만)
    @Query("""
    	    SELECT d FROM Document d
    	    WHERE d.documentType IN (
    	        SELECT dp.documentType FROM DocumentProcess dp
    	        WHERE dp.department = :department
    	    )
    	    AND d.status = 'APR'
    	""")
    	Page<Document> findProcessableDocList(@Param("department") Department department, Pageable pageable);
    
}