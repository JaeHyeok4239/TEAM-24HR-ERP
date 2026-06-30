package com.hr24.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.document.entity.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long>{
	
	@Query("select lt.typeId from LeaveType lt where lt.typeName = :typeName")
	Long findTypeIdByTypeName(@Param("typeName") String typeName);
	
}
