package com.hr24.attendance.utils;

import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.entity.AttendanceResult;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {
	
	private static final long STANDARD_WORK_MINUTES = 480; // 8시간
	
	// 휴게 시간 계산
	public static long getBreakTime(long totalWorkTime, boolean isHoliday) {
        if (!isHoliday) {
            return 60;
        }
        // 공휴일
        if (totalWorkTime >= 480) return 60; // 8시간 이상 - 1시간
        if (totalWorkTime >= 240) return 30; // 4시간 이상 - 30분
        return 0; // 4시간 미만 - 휴게 없음
    }	

	// 기본 근무 시간(최대 8시간까지만 기본 근무로 인정)
	public static long calculateBasicTime(long totalWorkTime, boolean isHoliday) {
        long breakTime = getBreakTime(totalWorkTime, isHoliday);
        long effectiveWorkTime = Math.max(0, totalWorkTime - breakTime);
        return Math.min(effectiveWorkTime, STANDARD_WORK_MINUTES);
    }

	// 초과 근무 시간
	public static long calculateOvertime(long totalWorkTime, boolean isHoliday) {
        long breakTime = getBreakTime(totalWorkTime, isHoliday);
        long effectiveWorkTime = Math.max(0, totalWorkTime - breakTime);
        return Math.max(0, effectiveWorkTime - STANDARD_WORK_MINUTES);
    }
	
	// 총 근무 시간
    public static long calculateTotalTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return Duration.between(start, end).toMinutes();
    }
}