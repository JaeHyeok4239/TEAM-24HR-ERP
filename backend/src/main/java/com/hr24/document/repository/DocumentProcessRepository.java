package com.hr24.document.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.DocumentProcess;

public interface DocumentProcessRepository extends JpaRepository<DocumentProcess, Long>{
	
}
