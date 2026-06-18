package com.hr24.document.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;

import com.hr24.employee.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_document_seq")
	@SequenceGenerator(name = "approval_document_seq", sequenceName = "approval_document_seq", allocationSize = 1)
	private Long documentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_type")
	private DocumentType documentType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_id")
	private User requester;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "processor_id")
	private User processor;
	
	@Column(name = "document_title")
	private String documentTitle;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "document_content")
	private Map<String, Object> documentContent;
	
	@Column(name = "status")	
	private String status;
	
	@Column(name = "current_step", insertable = false)
	private Integer currentStep;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@Column(name = "requested_at")
	private LocalDateTime requestedAt;
	
	@Column(name = "processed_at")
	private LocalDateTime processedAt;
	
	@Column(name = "reject_reason")
	private String rejectReason;
	
	@Version
	private Long version;
}
