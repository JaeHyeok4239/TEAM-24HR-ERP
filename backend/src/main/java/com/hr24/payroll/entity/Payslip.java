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
@Table(name = "payslips")
@Getter
@Setter
@NoArgsConstructor
public class Payslip {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "payslips_generator"
	)
	@SequenceGenerator(
		name = "payslips_generator",
		sequenceName = "payslips_seq",
		allocationSize = 1
	)
	@Column(name = "payslip_id")
	private Long payslipId;
	
	@Column(name = "payroll_id")
	private Long payrollId;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "file_path")
	private String filePath;
	
	@Column(name = "file_type")
	private String fileType;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
}
