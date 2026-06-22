package com.hr24.attendance.utils;

import com.hr24.attendance.entity.AttendanceResult;
import java.time.Duration;

public class TimeUtils {

    // 총 근무 시간 계산(출근~퇴근 차이 분 단위)
    public static long calculateTotalTime(AttendanceResult result) {
        if (result.getCheckInTime() == null || result.getCheckOutTime() == null) {
            return 0;
        }
        return Duration.between(result.getCheckInTime(), result.getCheckOutTime()).toMinutes();
    }

    // 기본 근무 시간 계산(휴게 60분 차감 로직)
    public static long calculateBasicTime(long totalWorkTime) {
        return Math.max(0, totalWorkTime - 60); 
    }
}