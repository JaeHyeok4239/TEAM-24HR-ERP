package com.hr24.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.Workplace;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long>{
	
}
