package com.hr24.document.entity;


import com.hr24.global.attachment.Attachment;

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
@Table(name="document_attach_mapping")
public class DocumentAttachMapping {
	
	@Id
	@Column(name = "doc_mapping_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_attach_mapping_seq")
	@SequenceGenerator(name = "document_attach_mapping_seq", sequenceName = "document_attach_mapping_seq", allocationSize = 1)
	private Long docMappingId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	private Document document;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_id")
	private Attachment attachment;
	
	public String getOriginalFileName() {
		return this.attachment.getOriginalName();
	}
	
	public String getFileType() {
		return this.attachment.getAttachmentType();
	}

}
