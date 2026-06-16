package com.hr24.payroll.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hr24.payroll.entity.Payroll;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

	List<Payroll> findByPayMonth(String payMonth);

    List<Payroll> findByUser_EmployeeId(Long employeeId);

    List<Payroll> findByStatus(String status);
    
    @Query("""
            SELECT p
            FROM Payroll p
            JOIN FETCH p.user u
            JOIN FETCH u.department
            """)
    
    List<Payroll> findAllWithEmployee();
}
