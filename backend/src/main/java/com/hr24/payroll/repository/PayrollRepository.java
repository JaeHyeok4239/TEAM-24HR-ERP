package com.hr24.payroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.payroll.dto.DepartmentCostDto;
import com.hr24.payroll.dto.MonthlyCostDto;
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
    	Page<Payroll> searchPayrolls(
    	        @Param("month") String month,
    	        @Param("employeeNo") String employeeNo,
    	        @Param("departmentId") Long departmentId,
    	        Pageable pageable
    	);
    
    @Query("""
    	    SELECT p
    	    FROM Payroll p
    	    JOIN FETCH p.user u
    	    JOIN FETCH u.department
    	    WHERE p.payrollId = :payrollId
    	""")
    	Optional<Payroll> findDetailById(@Param("payrollId") Long payrollId);
    
    
    @Query("""
    	    SELECT new com.hr24.payroll.dto.MonthlyCostDto(p.payMonth, SUM(p.totalPay))
    	    FROM Payroll p
    	    GROUP BY p.payMonth
    	    ORDER BY p.payMonth
    	""")
    	List<MonthlyCostDto> getMonthlyCost();
    
    
    @Query("""
    	    SELECT new com.hr24.payroll.dto.DepartmentCostDto(    	        
    	        d.departmentName,
    	        SUM(p.totalPay)
    	    )
    	    FROM Payroll p
    	    JOIN p.user u
    	    JOIN u.department d
    	    WHERE p.payMonth = :month
    	    GROUP BY d.departmentName
    	    ORDER BY SUM(p.totalPay) DESC
    	""")
    	List<DepartmentCostDto> getDepartmentCost(@Param("month") String month);
}
