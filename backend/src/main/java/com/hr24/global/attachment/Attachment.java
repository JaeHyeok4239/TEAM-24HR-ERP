package com.hr24.global.attachment;

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

//공통 파일 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="attachment")
public class Attachment {
	
	@Id
	@Column(name = "attachment_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq")
	@SequenceGenerator(name = "attachment_seq", sequenceName = "attachment_seq", initialValue = 1, allocationSize = 1 )
	private Long attachmentId;
	
	@Column(name = "original_name")
	private String originalName;
	
	@Column(name = "storedName")
	private String storedName;
	
	@Column(name = "attachment_type")
	private String attachmentType;
	
	@Column(name = "upload_time")
	private LocalDateTime uploadTime;
	
	@Column(name = "attachment_size")
	private Long attachmentSize;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uploader", nullable = false)
	private User uploader;
}
