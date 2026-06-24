package com.hr24.attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AnnualLeaveBalances;
import com.hr24.employee.entity.User;

public interface AnnualLeaveBalancesRepository extends JpaRepository<AnnualLeaveBalances, Long>{

	Optional<AnnualLeaveBalances> findByEmployeeAndLeaveYear(User employee, Integer leaveYear);
}
