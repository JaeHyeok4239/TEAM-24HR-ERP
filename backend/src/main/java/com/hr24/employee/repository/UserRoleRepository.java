package com.hr24.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	
	List<UserRole> findByEmployeeId(Long employeeId);

}
