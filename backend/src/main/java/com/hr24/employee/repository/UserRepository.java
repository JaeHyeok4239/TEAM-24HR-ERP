package com.hr24.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLoginId(String loginId);
	
	Optional<User> findByEmployeeNo(String employeeNo);

    List<User> findByNameContaining(String keyword);

    List<User> findByDepartment_DepartmentId(Long departmentId);
}