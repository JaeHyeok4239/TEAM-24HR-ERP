package com.hr24.attendance.scheduler;

import java.time.LocalDate;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceService attendanceService;
    private final AttendanceResultRepository attendanceResultRepository;

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
    
    // [혹시 모를 시연용] 서버가 오전 6시 오후 11시에 켜질 순 없기에 누락 배치 체크 로직 추가
    // MissingCheckout이 getCurrentTime을 사용하기에 데이터 오염에 유의
//    @EventListener(ApplicationReadyEvent.class)
//    public void checkAndRunBatchOnStartup() {
//        LocalDate today = LocalDate.now();
//        
//        // 오늘 날짜 기준 배치 돌았는지 확인
//        boolean isBatchAlreadyRun = attendanceResultRepository.existsByWorkDate(today);
//        
//        if (!isBatchAlreadyRun) {
//            // 오후 배치 실행 - 어제의 미퇴근 기록 처리
//            attendanceService.processMissingCheckouts(); 
//            
//            // 오전 배치 실행 - 오늘자 근태 기록 생성
//            attendanceService.createDailyAttendanceResults();
//            
//            System.out.println(">>> 서버 시작 시 미수행된 배치 프로그램들을 순차적으로 실행했습니다.");
//        }
//    }
}