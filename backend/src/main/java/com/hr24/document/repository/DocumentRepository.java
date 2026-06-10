package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long>{

}
