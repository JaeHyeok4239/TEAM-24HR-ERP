package com.hr24.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
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
	
	@Column(name="attendance_status_id")
	private Long attendanceStatusId;
	
	@Column(name="attendance_threshold_id")
	private Long attendanceThresholdId;
	
	@Column(name="approval_status_id")
	private Long approvalStatusId;
	
	@Column(name="half_day_type_id")
	private Long halfDayTypeId;
	
	@Column(name="holiday_id")
	private Long holidayId;
	
	@Column(name="correction_type_id")
	private Long correctionTypeId; 
	
	@Column(name="correction_reason_type_id")
	private Long correctionReasonTypeId;
	
	@Column(name="employee_id")
	private Long employeeId;
	
	@Column(name="work_date")
	private LocalDate workDate;
	
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
	
	@Column(name="is_correction_required")
	private String isCorrectionRequired;
	
	@Column(name="processing_status")
	private String processingStatus;
	
	@Column(name="correction_reason")
	private String correctionReason;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}