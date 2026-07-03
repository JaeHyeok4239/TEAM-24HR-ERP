package com.hr24.attendance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
//@Builder
//@NoArgsConstructor 
//@AllArgsConstructor
@SequenceGenerator(
		name="attendance_thresholds_seq",
		sequenceName="attendance_thresholds_seq",
		initialValue = 1,
		allocationSize = 1
		)
@Table(name="attendance_thresholds")
public class AttendanceThreshold{
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_thresholds_seq"
			)
	@Column(name="attendance_threshold_id")
	private Long attendanceThresholdId;
	
	@Column(name="employment_type")
	private String employmentType;
	
	@Column(name="threshold_type")
	private String thresholdType;
	
	@Column(name="threshold_minutes")
	private Long thresholdMinutes;
	
	@Column(name="threshold_description")
	private String thresholdDescription;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}