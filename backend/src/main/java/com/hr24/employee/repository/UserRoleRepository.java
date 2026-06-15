package com.hr24.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.employee.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	
	@Query("""
		select ur
		from UserRole ur
		join fetch ur.role
		where ur.user.employeeId = :employeeId
	""")
	List<UserRole> findAllWithRoleByEmployeeId(
			@Param("employeeId") Long employeeId
	);	
	
}
