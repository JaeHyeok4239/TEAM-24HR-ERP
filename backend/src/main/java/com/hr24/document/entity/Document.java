package com.hr24.document.entity;

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
@Table(name="document")
public class Document {
	
	@Id
	@Column(name = "document_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
	@SequenceGenerator(name = "document_seq", sequenceName = "document_seq", allocationSize = 1)
	private Long documentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_type")
	private DocumentType documentType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_id")
	private User requesterId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processor_id")
	private User processorId;
	
	@Column(name = "document_title")
	private String documentTitle;
	
	@Column(name = "status")	
	private String status;
	
	@Column(name = "current_step")
	private Integer currentStep;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@Column(name = "requested_at")
	private LocalDateTime requestedAt;
	
	@Column(name = "processed_at")
	private LocalDateTime processedAt;
	
	@Column(name = "reject_reason")
	private String rejectReason;
	
}
