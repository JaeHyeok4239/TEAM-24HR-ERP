package com.hr24.approval.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Long historyId;
}
