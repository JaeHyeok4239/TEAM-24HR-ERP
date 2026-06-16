package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Long>{

}
