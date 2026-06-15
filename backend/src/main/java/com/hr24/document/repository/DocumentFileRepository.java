package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.DocumentFile;

public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{

}
