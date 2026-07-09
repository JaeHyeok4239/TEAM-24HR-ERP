package com.hr24.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hr24.document.entity.Leave;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.work.schedule.entity.Holiday;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hr24.attendance.entity.Workplace;
import com.hr24.attendance.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "attendance_results_seq", sequenceName = "attendance_results_seq", initialValue = 1, allocationSize = 1)
@Table(name = "attendance_results")
public class AttendanceResult {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_results_seq")
	@Column(name = "attendance_result_id")
	private Long attendanceResultId;

	@Enumerated(EnumType.STRING)
	@Column(name = "attendance_status")
	private AttendanceStatus attendanceStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private User employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leave_id")
	private Leave leave;

	@Column(name = "work_date")
	private LocalDate workDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workplace_id")
	private Workplace workplace;

	@Column(name = "check_in_time")
	private LocalDateTime checkInTime;

	@Column(name = "check_out_time")
	private LocalDateTime checkOutTime;

	@Column(name = "total_work_minutes")
	private Long totalWorkMinutes;

	@Column(name = "actual_work_minutes")
	private Long actualWorkMinutes;

	@Column(name = "overtime_minutes")
	private Long overtimeMinutes;

	@Column(name = "is_holiday_work")
	private String isHolidayWork;

	@Column(name = "is_checkout_missing")
	private String isCheckoutMissing;

	@Column(name = "is_fixed")
	private String isFixed;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// 출퇴근 정정 IN/OUT 분류용
	public LocalDateTime getTimeByType(String type) {
		if ("IN".equals(type)) {
			return this.checkInTime;
		} else if ("OUT".equals(type)) {
			return this.checkOutTime;
		}
		throw new IllegalArgumentException("올바르지 않은 정정 타입입니다: " + type);
	}
}