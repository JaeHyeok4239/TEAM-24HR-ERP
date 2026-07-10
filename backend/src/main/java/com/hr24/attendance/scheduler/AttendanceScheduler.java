package com.hr24.attendance.scheduler;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hr24.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
// 오전 6시, 오후 11시 배치 프로그램
public class AttendanceScheduler {

    private final AttendanceService attendanceService;

    // 매일 밤 11시에 실행
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void scheduleMissingCheckoutUpdate() {
        attendanceService.processMissingCheckouts(); 
    }

    // 매일 오전 6시에 실행
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void scheduleDailyAttendanceCreation() {
        attendanceService.createDailyAttendanceResults();
    }
}