package com.hr24.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLogDaily;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
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
// 일용직 근태 수정
public class AttendanceCorrectionService {
    private final AttendanceCorrectionRepository attendanceCorrectionRepository;
    private final UserRepository userRepository;
    private final AttendanceLogDailyRepository attendanceLogsDailyRepository;
   
    @Transactional
    public void applyDailyCorrection(Long logId, List<DailyCorrectionDto> dtoList, String loginId) {
        User processor = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        AttendanceLogDaily dailyLog = attendanceLogsDailyRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("일용직 기록을 찾을 수 없습니다."));

        // 출퇴근 한번에 수정할 시 List로 받아옴
        for (DailyCorrectionDto dto : dtoList) {
        	if (dto.getAfterTime() == null) continue; // 데이터 무결성 체크
        	
        	// 수정 전 데이터 저장
            LocalDateTime beforeTime = "IN".equals(dto.getCorrectionType()) ? dailyLog.getCheckInTime() : dailyLog.getCheckOutTime();
            
            // 데이터 수정
            if ("IN".equalsIgnoreCase(dto.getCorrectionType())) {
                dailyLog.setCheckInTime(dto.getAfterTime());
            } else if ("OUT".equalsIgnoreCase(dto.getCorrectionType())) {
                dailyLog.setCheckOutTime(dto.getAfterTime());
            }
            
            // 이력 저장
            saveCorrectionRecord(null, dailyLog, dto.getCorrectionType(), beforeTime, dto.getAfterTime(), dto.getCorrectionReason(), processor);
        }
        dailyLog.setUpdatedAt(LocalDateTime.now()); 
        attendanceLogsDailyRepository.save(dailyLog);
    }

    // 근태 정정 이력 저장
    private void saveCorrectionRecord(
    		AttendanceResult result, AttendanceLogDaily dailyLog, 
    		String corrType, 
            LocalDateTime beforeTime, LocalDateTime afterTime, 
            String reason, User processor) {
        
        AttendanceCorrection correction = AttendanceCorrection.builder()
                .correctionTarget(result)
                .correctionDailyLog(dailyLog)
                .correctionType(corrType)
                .beforeTime(beforeTime)
                .afterTime(afterTime)
                .correctionReason(reason)
                .isProcessed("Y")
                .processedBy(processor)
                .build();

        attendanceCorrectionRepository.save(correction);
    }
}