package com.hr24.payroll.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_allowance")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeAllowance {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "employee_allowance_generator"
	)
	@SequenceGenerator(
		name = "employee_allowance_generator",
		sequenceName = "employee_allowance_seq",
		allocationSize = 1
	)
	@Column(name = "employee_allowance_id")
	private Long employeeAllowanceId;
	
	@Column(name = "employee_id")
    private Long employeeId;
	
	@Column(name = "allowance_item_id")
	private Long allowanceItemId;
	
	@Column(name = "amount")
	private Long amount;
}
