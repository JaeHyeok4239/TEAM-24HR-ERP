package com.hr24.attendance.service;


import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.entity.AttendanceThreshold;
import com.hr24.attendance.entity.AttendanceTimePolicy;
import com.hr24.attendance.repository.AttendanceThresholdRepository;
import com.hr24.attendance.repository.AttendanceTimePolicyRepository;
import com.hr24.employee.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
//출퇴근 시간 판정
public class AttendanceCalculator{
	private final AttendanceTimePolicyRepository attendanceTimePolicyRepository;
    private final AttendanceThresholdRepository attendanceThresholdRepository;
    
    public String calculateStatus(User user, AttendanceResult result, String type) {
        if ("IN".equals(type)) {
            // 출근 시간 판정 호출
            return determineCheckInStatus(user, result.getCheckInTime());
        } else {
            // 퇴근 시간 판정 호출
            return determineCheckoutStatus(user, result.getCheckOutTime());
        }
    }

    // 출근
    public String determineCheckInStatus(User user, LocalDateTime logTime) {
    	// 근무 기준 확인(WORK 900 ~ 1800)
        AttendanceTimePolicy policy = attendanceTimePolicyRepository
                .findByEmploymentTypeAndPolicyType(user.getEmploymentType().name(), "WORK")
                .orElseThrow(() -> new RuntimeException("근무 정책이 설정되지 않았습니다."));
        
        LocalTime policyStartTime = convertToLocalTime(policy.getStartTime());
        LocalTime actualTime = logTime.toLocalTime();

        AttendanceThreshold lateThreshold = attendanceThresholdRepository
                .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "LATE").orElse(null);
        AttendanceThreshold absenceThreshold = attendanceThresholdRepository
                .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "ABSENCE").orElse(null);

        long diffMinutes = ChronoUnit.MINUTES.between(policyStartTime, actualTime);

        // 시간에 따른 상태 코드 판정
        if (diffMinutes <= 0) return "WORK";
        if (absenceThreshold != null && diffMinutes >= absenceThreshold.getThresholdMinutes()) return "ABSENCE";
        if (lateThreshold != null && diffMinutes > lateThreshold.getThresholdMinutes()) return "LATE";

        return "WORK"; // 외의 상황들(지각 기준을 5분으로 바꿨을 때 그 비는 시간은 어떻게 할 것인지 등)
    }

    // 퇴근
    public String determineCheckoutStatus(User user, LocalDateTime logTime) {
    	// 근무 기준 확인(WORK 900 ~ 1800)
        AttendanceTimePolicy policy = attendanceTimePolicyRepository
                .findByEmploymentTypeAndPolicyType(user.getEmploymentType().name(), "WORK")
                .orElseThrow(() -> new RuntimeException("근무 정책이 설정되지 않았습니다."));
        
        LocalTime policyEndTime = convertToLocalTime(policy.getEndTime());
        LocalTime actualTime = logTime.toLocalTime();

        AttendanceThreshold earlyLeaveThreshold = attendanceThresholdRepository
                .findByEmploymentTypeAndThresholdType(user.getEmploymentType().name(), "EARLY_LEAVE").orElse(null);

        // 시간에 따른 상태 코드 판정
        if (actualTime.isBefore(policyEndTime)) {
            long diffMinutes = ChronoUnit.MINUTES.between(actualTime, policyEndTime);
            if (earlyLeaveThreshold != null && diffMinutes >= earlyLeaveThreshold.getThresholdMinutes()) {
                return "EARLY_LEAVE";
            }
        }
        return "OUT";
    }

    // 서비스에서 쓰던 convertToLocalTime
    private LocalTime convertToLocalTime(Long timeNumber) {
        if (timeNumber == null) throw new RuntimeException("근무 기준 값이 설정되지 않았습니다.");
        return LocalTime.of((int)(timeNumber / 100), (int)(timeNumber % 100));
    }
}