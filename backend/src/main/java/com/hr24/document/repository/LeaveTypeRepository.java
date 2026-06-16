package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.document.entity.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long>{

}
