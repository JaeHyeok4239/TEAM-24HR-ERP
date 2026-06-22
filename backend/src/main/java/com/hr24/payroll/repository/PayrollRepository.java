package com.hr24.payroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("""
    	    SELECT p
    	    FROM Payroll p
    	    JOIN FETCH p.user u
    	    JOIN FETCH u.department d
    	    WHERE (:month IS NULL OR p.payMonth = :month)
    	    AND (:employeeNo IS NULL OR u.employeeNo = :employeeNo)
    	    AND (:departmentId IS NULL OR d.departmentId = :departmentId)
    	    ORDER BY p.payMonth DESC
    	""")
    	List<Payroll> searchPayrolls(
    	        @Param("month") String month,
    	        @Param("employeeNo") String employeeNo,
    	        @Param("departmentId") Long departmentId
    	);
    
    @Query("""
    	    SELECT p
    	    FROM Payroll p
    	    JOIN FETCH p.user u
    	    JOIN FETCH u.department
    	    WHERE p.payrollId = :payrollId
    	""")
    	Optional<Payroll> findDetailById(@Param("payrollId") Long payrollId);
}
