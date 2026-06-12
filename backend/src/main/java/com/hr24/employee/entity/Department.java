package com.hr24.employee.entity;

import java.time.LocalDateTime;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "departments")
public class Department {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "department_generator"
	)
	@SequenceGenerator(
	    name = "department_generator",
	    sequenceName = "department_seq",
	    allocationSize = 1
	)
	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "department_code", nullable = false, length = 50, unique = true)
	private String departmentCode;

	@Column(name = "department_name", nullable = false, length = 100)
	private String departmentName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_department_id")
	private Department parentDepartment;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private String isActive = "Y";

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
