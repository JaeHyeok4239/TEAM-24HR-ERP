package com.hr24.payroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hr24.payroll.dto.SalaryResponseDto;
import com.hr24.payroll.entity.Salary;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Optional<Salary> findByEmployeeEmployeeId(Long employeeId);
    
    
    @Query("""
        SELECT new com.hr24.payroll.dto.SalaryResponseDto(
            u.employeeId,
            u.employeeNo,
            u.name,
            d.departmentName,
            s.salaryId,
            s.baseSalary
        )
        FROM User u
        LEFT JOIN Salary s
            ON s.employee.employeeId = u.employeeId
        LEFT JOIN u.department d
        ORDER BY u.employeeNo
    """)
    List<SalaryResponseDto> findSalaryList();
}