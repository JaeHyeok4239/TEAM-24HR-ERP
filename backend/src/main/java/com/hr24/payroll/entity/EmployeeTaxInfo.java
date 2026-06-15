package com.hr24.payroll.entity;

import java.time.LocalDateTime;

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
@Table(name = "employee_tax_info")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeTaxInfo {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "employee_tax_info_generator"
	)
	@SequenceGenerator(
		name = "employee_tax_info_generator",
		sequenceName = "employee_tax_info_seq",
		allocationSize = 1
	)
	@Column(name = "tax_info_id")
	private Long taxInfoId;
	
	@Column(name = "employee_id")
    private Long employeeId;
	
	@Column(name = "dependents")
    private Long dependents;
	
	@Column(name = "children")
    private Long children;
	
	@Column(name = "withholding_rate")
    private Double withholdingRate;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
