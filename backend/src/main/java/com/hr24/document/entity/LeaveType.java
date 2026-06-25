package com.hr24.document.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="leave_type")
public class LeaveType {
	
	@Id
	@Column(name = "type_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_seq")
	@SequenceGenerator(name = "leave_seq", sequenceName = "leave_seq", allocationSize = 1)
	private Long leaveId;
	
	@Column(name = "type_name", nullable = false)
	private String typeName;
	
	@Column(name = "is_paid", nullable = false)
	private String isPaid;
}
