package com.hr24.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
// 일용직 근태 정정(관리자만 변경 가능)
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository attendanceCorrectionRepository;
    private final AttendanceLogDailyRepository attendanceLogsDailyRepository;
    private final UserRepository userRepository;
    
    // 일용직 수정 시(관리자 직접 수정)
    @Transactional
    public void correctDaily(Long logId, DailyCorrectionDto dto, String loginId) {
        // 수정할 로그 찾음(IN인지 OUT인지 결정)
        AttendanceLogsDaily log = attendanceLogsDailyRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("근태 기록을 찾을 수 없습니다."));

        // 관리자 정보 조회
        User processor = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 정정 유형(IN/OUT)에 따라 수정 전 시간 추출 및 시간 업데이트
        LocalDateTime beforeTime;
        
        if ("IN".equals(dto.getCorrectionType())) {
            beforeTime = log.getCheckInTime(); // 수정 전 IN 시간
            log.setCheckInTime(dto.getAfterTime()); // IN 시간 변경
        } else if ("OUT".equals(dto.getCorrectionType())) {
            beforeTime = log.getCheckOutTime(); // 수정 전 OUT 시간
            log.setCheckOutTime(dto.getAfterTime()); // OUT 시간 변경
        } else {
            throw new IllegalArgumentException("정정 유형(IN 또는 OUT)이 올바르지 않습니다.");
        }

        // 정정 이력 테이블에 저장
        AttendanceCorrection correction = AttendanceCorrection.builder()
                .correctionDailyLog(log) 
                .correctionType(dto.getCorrectionType()) // IN 또는 OUT 기록
                .beforeTime(beforeTime)
                .afterTime(dto.getAfterTime())
                .correctionReason(dto.getCorrectionReason())
                .isProcessed("Y")
                .processedBy(processor)
                .build();
                
        attendanceCorrectionRepository.save(correction);
    }
    

}