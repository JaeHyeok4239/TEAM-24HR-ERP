package com.hr24.employee.service;

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

		department.setDepartmentCode("TEST1");
		department.setDepartmentName("테스트부서2");
		department.setDescription("JPA save 테스트용 부서2");
		department.setIsActive("Y");

		return departmentRepository.save(department);
	}

}
