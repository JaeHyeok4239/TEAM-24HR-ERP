package com.hr24.employee.entity;

import com.hr24.employee.enums.EmployeeHistoryItem;
import com.hr24.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@NoArgsConstructor
@Entity
@Table(name = "employee_histories")
public class EmployeeHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_history_generator")
	@SequenceGenerator(
			name = "employee_history_generator",
			sequenceName = "employee_history_seq",
			allocationSize = 1
	)
	@Column(name = "employee_history_id")
	private Long employeeHistoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private User employee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "changed_by_employee_id")
	private User changedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "change_item", nullable = false, length = 50)
	private EmployeeHistoryItem changeItem;

	@Column(name = "before_value", length = 500)
	private String beforeValue;

	@Column(name = "after_value", length = 500)
	private String afterValue;

	@Column(name = "change_reason", nullable = false, length = 500)
	private String changeReason;

	public static EmployeeHistory create(
			User employee,
			User changedBy,
			EmployeeHistoryItem changeItem,
			String beforeValue,
			String afterValue,
			String changeReason
	) {
		EmployeeHistory history = new EmployeeHistory();

		history.employee = employee;
		history.changedBy = changedBy;
		history.changeItem = changeItem;
		history.beforeValue = beforeValue;
		history.afterValue = afterValue;
		history.changeReason = changeReason;

		return history;
	}
}