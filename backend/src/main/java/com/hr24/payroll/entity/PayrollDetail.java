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
@Table(name = "payroll_details")
@Getter
@Setter
@NoArgsConstructor
public class PayrollDetail {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "payroll_details_generator"
	)
	@SequenceGenerator(
		name = "payroll_details_generator",
		sequenceName = "payroll_details_seq",
		allocationSize = 1
	)
	@Column(name = "payroll_details_id")
	private Long payrollDetailsId;
	
	@Column(name = "payroll_id")
	private Long payrollId;
	
	@Column(name = "item_type")
	private String itemType;
	
	@Column(name = "item_id")
	private Long itemId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "amount")
	private Long amount;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
