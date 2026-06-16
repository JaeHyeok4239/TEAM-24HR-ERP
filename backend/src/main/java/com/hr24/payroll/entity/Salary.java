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
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
public class Salary {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "salary_generator"
	)
	@SequenceGenerator(
		name = "salary_generator",
		sequenceName = "salary_seq",
		allocationSize = 1
	)
	@Column(name = "salary_id")
	private Long salaryId;
	
	@Column(name = "employee_id")
    private Long employeeId;
	
	@Column(name = "base_salary")
    private Long baseSalary;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
