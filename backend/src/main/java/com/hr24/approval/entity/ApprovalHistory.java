package com.hr24.approval.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentFile;
import com.hr24.document.entity.DocumentType;
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
@Table(name="approval_history")
public class ApprovalHistory {
	
	@Id
	@Column(name = "history_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_history_seq")
	@SequenceGenerator(name = "approval_history_seq", sequenceName = "approval_history_seq", allocationSize = 1)
	private Long historyId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	private Document document;
	
	@Column(name = "step_order")
	private Integer stepOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approver_id")
	private User approver;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "approver_comment")
	private String approverComment;
	
	@Column(name = "acted_at")
	private LocalDateTime actedAt;
	
	@Column(name = "created_at")
	private LocalDateTime createAt;
}
