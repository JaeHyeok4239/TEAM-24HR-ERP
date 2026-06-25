package com.hr24.payroll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.payroll.entity.PayrollDetail;

public interface PayrollDetailRepository extends JpaRepository<PayrollDetail, Long> {

    List<PayrollDetail> findByPayrollPayrollId(Long payrollId);
}
