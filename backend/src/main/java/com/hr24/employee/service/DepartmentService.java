package com.hr24.employee.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.employee.entity.Department;
import com.hr24.employee.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

	private final DepartmentRepository departmentRepository;

	public List<Department> findAllDepartments() {
		return departmentRepository.findAll();
	}

	public Department createTestDepartment() {
		Department department = new Department();

		department.setDepartmentCode("TEST");
		department.setDepartmentName("테스트부서");
		department.setDescription("JPA save 테스트용 부서");
		department.setIsActive("Y");
		department.setCreatedAt(LocalDateTime.now());

		return departmentRepository.save(department);
	}

}
