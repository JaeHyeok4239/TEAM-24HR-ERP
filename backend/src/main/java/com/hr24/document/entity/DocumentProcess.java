package com.hr24.document.entity;



import com.hr24.employee.entity.Department;

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
@Table(name="document_process")
public class DocumentProcess {
	//문서 종류 별 처리 권한 있는 부서 연결
	@Id
	@Column(name = "document_process_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_process_seq")
	@SequenceGenerator(name = "document_process_seq", sequenceName = "document_process_seq", allocationSize = 1)
	private Long processId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private DocumentType documentType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Department department;
	
}
