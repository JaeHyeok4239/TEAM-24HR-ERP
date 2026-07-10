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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder                 
@NoArgsConstructor       
@AllArgsConstructor

@Entity
@Getter
@Setter
@Table(name = "attendance_logs_daily")
@SequenceGenerator(
		name="attendance_logs_daily_seq",
		sequenceName = "attendance_logs_daily_seq",
		initialValue = 1,
		allocationSize = 1
		)

public class AttendanceLogDaily{
	@Id
	@Column(name="attendance_logs_daily_id")
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_logs_daily_seq")
	private Long attendanceLogsDailyId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="employee_id")
	private User employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="workplace_id")
	private Workplace workplace;
	
	@Column(name="work_date")
	private LocalDate workDate;
	
	@Column(name="check_in_time")
	private LocalDateTime checkInTime;
	
	@Column(name="check_out_time")
	private LocalDateTime checkOutTime;
	
	@Column(name="is_attended")
	private String isAttended;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}