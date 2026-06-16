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
@Table(name = "allowance_items")
@Getter
@Setter
@NoArgsConstructor
public class AllowanceItem {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "allowance_items_generator"
	)
	@SequenceGenerator(
		name = "allowance_items_generator",
		sequenceName = "allowance_items_seq",
		allocationSize = 1
	)
	@Column(name = "allowance_item_id")
	private Long allowanceItemId;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "amount")
	private Long amount;
}
