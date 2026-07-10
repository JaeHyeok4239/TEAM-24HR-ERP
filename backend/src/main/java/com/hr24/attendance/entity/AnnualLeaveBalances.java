package com.hr24.attendance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hr24.employee.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="annual_leave_balances")
//직원별 보유 연차
public class AnnualLeaveBalances {

	@Id
	@Column(name = "annual_leave_balance_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annual_leave_balance_seq")
	@SequenceGenerator(name = "annual_leave_balance_seq", sequenceName = "annual_leave_balance_seq", allocationSize = 1)
	private Long annualLeaveBalanceId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private User employee;
	
	@Column(name = "leave_year")
	private Integer leaveYear;
	
	@Column(name = "total_days")
	private BigDecimal totalDays;
	
	@Column(name = "remaining_days")
	private BigDecimal remainingDays;
	
	@Column(name = "granted_at")
	private LocalDateTime grantedAt;
	
	@Column(name = "expires_at")
	private LocalDateTime expiresAt;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
