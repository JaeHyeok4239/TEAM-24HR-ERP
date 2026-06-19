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
		name="attendance_time_policies_seq",
		sequenceName="attendance_time_policies_seq",
		initialValue = 1,
		allocationSize = 1
		)
@Table(name="attendance_time_policies")
public class AttendanceTimePolicy{
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_time_policies_seq"
			)
	@Column(name="attendance_time_policy_id")
	private Long attendanceTimePolicyId;
	
	@Column(name="employment_type")
	private String employmentType;
	
	@Column(name="policy_type")
	private String policyType;
	
	@Column(name="start_time")
	private Long startTime;
	
	@Column(name="end_time")
	private Long endTime;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}