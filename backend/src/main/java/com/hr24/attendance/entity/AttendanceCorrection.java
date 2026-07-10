package com.hr24.attendance.entity;

import java.time.LocalDateTime;

import com.hr24.document.entity.Document;
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

@Builder                 
@NoArgsConstructor       
@AllArgsConstructor

@Entity
@Getter
@Setter
@Table(name = "attendance_correction")
@SequenceGenerator(
		name="attendance_correction_seq",
		sequenceName = "attendance_correction_seq",
		initialValue = 1,
		allocationSize = 1
		)

public class AttendanceCorrection{
	@Id
	@Column(name="attendance_correction_id")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_correction_seq")
	private Long attendanceCorrectionId;
	
	// 정규직
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="correction_target")
	private AttendanceResult correctionTarget;
	
	@Column(name="correction_type")
	private String correctionType;
	
	@Column(name="is_processed")
	private String isProcessed;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="processed_by")
	private User processedBy;
	
	@Column(length = 100, name="correction_reason")
	private String correctionReason;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="document_id")
	private Document document;
	
	@Column(name="before_time")
	private LocalDateTime beforeTime;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="correction_daily_log")
	private AttendanceLogDaily correctionDailyLog;

	@Column(name="after_time")
	private LocalDateTime afterTime;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}