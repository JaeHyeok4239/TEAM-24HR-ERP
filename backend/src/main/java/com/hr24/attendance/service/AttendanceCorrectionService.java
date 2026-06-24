package com.hr24.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
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
    private final AttendanceLogRepository attendanceLogRepository;
    private final UserRepository userRepository;
    
    // 일용직 수정 시(관리자 직접 수정)
    @Transactional
    public void correctDaily(Long logId, DailyCorrectionDto dto, String loginId) {
        // 수정할 로그 찾음(IN인지 OUT인지 결정)
        AttendanceLog log = attendanceLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("근태 기록을 찾을 수 없습니다."));

        // 현 시간을 beforeTime에 넣음
        LocalDateTime beforeTime = log.getLogTime();

        User processor = userRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        		
        // 로그 시간 수정
        log.setLogTime(dto.getAfterTime());

        // 정정 이력 테이블에 저장
        AttendanceCorrection correction = AttendanceCorrection.builder()
                .correctionDailyLog(log) // 이 로그(row)의 ID를 연결
                .correctionType(log.getLogType()) // IN인지 OUT인지 기록
                .beforeTime(beforeTime)
                .afterTime(dto.getAfterTime())
                .correctionReason(dto.getCorrectionReason())
                .isProcessed("Y")
                .processedBy(processor)
                .build();
                
        attendanceCorrectionRepository.save(correction);
    }
    

}