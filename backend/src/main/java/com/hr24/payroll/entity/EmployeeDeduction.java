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
@Table(name = "employee_deduction")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeDeduction {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "employee_deduction_generator"
	)
	@SequenceGenerator(
		name = "employee_deduction_generator",
		sequenceName = "employee_deduction_seq",
		allocationSize = 1
	)
	@Column(name = "employee_deduction_id")
	private Long employeeDeductionId;
	
	@Column(name = "employee_id")
    private Long employeeId;
	
	@Column(name = "deduction_item_id")
	private Long deductionItemId;
	
	@Column(name = "amount")
	private Long amount;
}
