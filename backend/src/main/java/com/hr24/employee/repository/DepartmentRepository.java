package com.hr24.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
