package com.hr24.payroll.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payrolls")
@Getter
@Setter
@NoArgsConstructor
public class Payroll {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "payrolls_generator"
	)
	@SequenceGenerator(
		name = "payrolls_generator",
		sequenceName = "payrolls_seq",
		allocationSize = 1
	)
	@Column(name = "payroll_id")
	private Long payrollId;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User user;
	
	@Column(name = "pay_month")
	private String payMonth;
	
	@Column(name = "total_pay")
	private BigDecimal totalPay;
	
	@Column(name = "total_deduction")
	private BigDecimal totalDeduction;
	
	@Column(name = "net_salary")
	private BigDecimal netSalary;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
