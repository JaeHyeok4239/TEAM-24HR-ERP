package com.hr24.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.dto.DocumentAttachMappingDto;
import com.hr24.document.entity.DocumentAttachMapping;

public interface DocumentAttachMappingRepository extends JpaRepository<DocumentAttachMapping, Long>{
	
	//문서에 연결되어있는 파일 목록
	//성능 최적화(N+1 이슈 방지)위해 자동으로 outerJoin 해주는 EntityGraph 어노테이션 사용
	@EntityGraph(attributePaths = {"document", "attachment"})
	@Query("select m from DocumentAttachMapping m where m.document.id = :documentId")
	List<DocumentAttachMapping> findAllByDocId(@Param("documentId") Long documentId);
	
}
