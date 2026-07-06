package com.hr24.document.entity;

import java.time.LocalDateTime;

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
@Table(name="document_type_schema")
public class DocumentTypeSchema {
	@Id
	@Column(name = "schema_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_type_schema_seq")
	@SequenceGenerator(name = "document_type_schema_seq", sequenceName = "document_type_schema_seq", allocationSize = 1)
	private Long schemaId;
	
	 @Column(name = "schema_json", nullable = false)
	    private String schemaJson;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "document_type", nullable = false)
	    private DocumentType documentType;

	    @Column(name = "created_at", insertable = false, updatable = false)
	    private LocalDateTime createdAt;

	    @Column(name = "updated_at")
	    private LocalDateTime updatedAt;
	    
	    public void updateSchemaJson(String schemaJson) {
	        this.schemaJson = schemaJson;
	    }
}
