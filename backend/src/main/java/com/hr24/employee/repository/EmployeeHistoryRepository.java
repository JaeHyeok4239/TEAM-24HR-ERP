package com.hr24.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.EmployeeHistory;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {

    List<EmployeeHistory> findByEmployee_EmployeeIdOrderByCreatedAtDesc(Long employeeId);
}