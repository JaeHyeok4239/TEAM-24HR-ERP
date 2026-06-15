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
@Table(name = "deduction_items")
@Getter
@Setter
@NoArgsConstructor
public class DeductionItem {
	
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "deduction_items_generator"
	)
	@SequenceGenerator(
		name = "deduction_items_generator",
		sequenceName = "deduction_items_seq",
		allocationSize = 1
	)
	@Column(name = "deduction_item_id")
	private Long deductionItemId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "ratio")
	private Double ratio;
}
