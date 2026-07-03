package com.hr24.approval.entity;


import com.hr24.document.entity.DocumentType;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;

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
@Table(name="approval_line")
public class ApprovalLine {
	@Id
	@Column(name = "approval_line_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_line_seq")
	@SequenceGenerator(name = "approval_line_seq", sequenceName = "approval_line_seq", allocationSize = 1)
	private Long approvalLineId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_type", nullable = false)
	private DocumentType documentType;
	
	@Column(name = "step_order", nullable = false)
	private Integer stepOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_approver", nullable = false)
	private User approver;
}
