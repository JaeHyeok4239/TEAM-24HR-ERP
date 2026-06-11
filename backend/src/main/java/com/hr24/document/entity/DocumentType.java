package com.hr24.document.entity;

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
@Table(name="document_type")
public class DocumentType {
	
	@Id
	@Column(name = "type_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_type_seq")
	@SequenceGenerator(name = "document_seq", sequenceName = "document_type_seq", allocationSize = 1)
	private Long typeId;
	
	@Column(name = "type_name")
	private String typeName;
	
	@Column(name = "detail_table")
	private String detailTable;
	
}
