package com.hr24.attendance.entity;

import java.time.LocalDate;
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
import lombok.NoArgsConstructor;

@Builder                 
@NoArgsConstructor       
@AllArgsConstructor

@Entity
@Table(name = "attendance_logs")
@SequenceGenerator(
		name="attendance_logs_seq",
		sequenceName = "attendance_logs_seq",
		initialValue = 1,
		allocationSize = 1
		)

public class AttendanceLog{
	@Id
	@Column(name="attendance_log_id")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_logs_seq")
	private Long attendanceLogId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="employee_id")
	private User employee;
	
	@Column(name="log_type")
	private String logType;
	
	@Column(name="log_time")
	private LocalDateTime logTime;
	
	@Column(name="latitude")
	private Double latitude;
	
	@Column(name="longitude")
	private Double longitude;
	
	@Column(name="is_location_valid")
	private String isLocationValid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="workplace_id")
	private Workplace workplace;
	
	@Column(name="work_date")
	private LocalDateTime workDate;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
	
	
}