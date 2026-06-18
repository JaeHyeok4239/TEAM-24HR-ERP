package com.hr24.attendance.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hr24.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceService attendanceService;

    // [1] 매일 밤 11시에 마감 로직 실행
    @Scheduled(cron = "0 0 23 * * *")
    public void scheduleMissingCheckoutUpdate() {
        
        attendanceService.processMissingCheckouts(); 
    }

    // [2] 매일 오전 6시에 데이터 생성 로직 실행
    @Scheduled(cron = "0 0 6 * * *")
    public void scheduleDailyAttendanceCreation() {
        
        attendanceService.createDailyAttendanceResults();
    }
}