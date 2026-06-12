package com.hr24.employee.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.entity.Department;
import com.hr24.employee.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
	private final DepartmentService departmentService;

	@GetMapping("/api/departments")
	public List<Department> getDepartments() {
		return departmentService.findAllDepartments();
	}
	
	@PostMapping("/api/departments/test")
	public Department createTestDepartment() {
	    return departmentService.createTestDepartment();
	}
}
