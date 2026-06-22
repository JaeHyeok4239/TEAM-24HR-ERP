package com.hr24.attendance.entity;

import java.time.LocalDateTime;

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

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@SequenceGenerator(
		name="attendance_results_seq",
		sequenceName="attendance_results_seq",
		initialValue = 1,
		allocationSize = 1
		)
@Table(name="attendance_results")
public class AttendanceResult{
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "attendance_results_seq"
			)
	@Column(name="attendance_result_id")
	private Long attendanceResultId;
	
	@Column(name="attendance_status")
	private String attendanceStatus;
	
	@Column(name="attendance_threshold_id")
	private Long attendanceThresholdId;
	
	@Column(name="holiday_id")
	private Long holidayId;
	
	@Column(name="employee_id")
	private Long employeeId;
	
	@Column(name="attendance_correction_id")
	private Long attendanceCorrectionId;
	
	@Column(name="leave_id")
	private Long leaveId;
	
	@Column(name="work_date")
	private LocalDateTime workDate;
	
	@Column(name="workplace_id")
	private Long workplaceId;
	
	@Column(name="check_in_time")
	private LocalDateTime checkInTime;
	
	@Column(name="check_out_time")
	private LocalDateTime checkOutTime;
	
	@Column(name="total_work_minutes")
	private Long totalWorkMinutes;
	
	@Column(name="actual_work_minutes")
	private Long actualWorkMinutes;
	
	@Column(name="overtime_minutes")
	private Long overtimeMinutes;
	
	@Column(name="is_holiday_work")
	private String isHolidayWork;
	
	@Column(name="is_missing_checkout")
	private String isMissingCheckout;
	
	@Column(name="is_fixed")
	private String isFixed;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}