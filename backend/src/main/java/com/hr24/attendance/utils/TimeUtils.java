package com.hr24.attendance.utils;

import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.entity.AttendanceResult;
import java.time.Duration;

public class TimeUtils {

    // 정규직 - 총 근무 시간 계산
    public static long calculateTotalTime(AttendanceResult result) {
        if (result.getCheckInTime() == null || result.getCheckOutTime() == null) {
            return 0;
        }
        return Duration.between(result.getCheckInTime(), result.getCheckOutTime()).toMinutes();
    }
    
    // 일용직 - 총 근무 시간 계산
    public static long calculateTotalTime(AttendanceLogsDaily dailyLog) {
        if (dailyLog.getCheckInTime() == null || dailyLog.getCheckOutTime() == null) {
            return 0;
        }
        return Duration.between(dailyLog.getCheckInTime(), dailyLog.getCheckOutTime()).toMinutes();
    }

    // 범용 - 기본 근무 시간 계산(휴게 60분 차감 로직)
    public static long calculateBasicTime(long totalWorkTime) {
        return Math.max(0, totalWorkTime - 60); 
    }
}