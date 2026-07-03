package com.hr24.approval.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name="approval_delegate")
public class ApprovalDelegate {
	
	@Id
	@Column(name = "approval_delegate_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_delegate_seq")
	@SequenceGenerator(name = "approval_delegate_seq", sequenceName = "approval_delegate_seq", allocationSize = 1)
	private Long approvalDelegateId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approver_id", nullable = false)
	private User approver;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delegate_id", nullable = false)
	private User delegate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approval_line_id", nullable = false)
	private ApprovalLine approvalLine;
	
	@Column(name = "reason", nullable = false)
	private String reason;
	
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;
	
	@Column(name = "is_active", nullable = false)
	private String isActive;
	
}
