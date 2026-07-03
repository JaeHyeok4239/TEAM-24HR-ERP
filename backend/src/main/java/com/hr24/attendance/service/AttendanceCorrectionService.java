package com.hr24.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.AttendanceCorrectionRequestDto;
import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceLogsDaily;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogDailyRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
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
// 근태 정정(관리자만 변경 가능)
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository attendanceCorrectionRepository;
    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AttendanceResultRepository resultRepository; // 정규직
    private final AttendanceLogDailyRepository attendanceLogsDailyRepository; //일용직
    
    // 일용직 수정
    // 일용직은 출퇴근을 한번에 수정할 수도 있으니 정규직과 합치지 않음
    @Transactional
    public void applyDailyCorrection(Long logId, List<DailyCorrectionDto> dtoList, String loginId) {
        User processor = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        AttendanceLogsDaily dailyLog = attendanceLogsDailyRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("일용직 기록을 찾을 수 없습니다."));

        // 출퇴근 한번에 수정할 시 List로 받아옴
        for (DailyCorrectionDto dto : dtoList) {
        	if (dto.getAfterTime() == null) continue; // 데이터 무결성 체크
        	
        	// 수정 전 데이터 저장
            LocalDateTime beforeTime = "IN".equals(dto.getCorrectionType()) ? dailyLog.getCheckInTime() : dailyLog.getCheckOutTime();
            
            // 데이터 수정
            if ("IN".equals(dto.getCorrectionType())) dailyLog.setCheckInTime(dto.getAfterTime());
            else dailyLog.setCheckOutTime(dto.getAfterTime());
            
            // 이력 저장
            saveCorrectionRecord(null, dailyLog, dto.getCorrectionType(), beforeTime, dto.getAfterTime(), dto.getCorrectionReason(), processor);
        }
    }

    // 정규직 정정(문서 승인 후 호출)
    public void applyRegularCorrection(Long logId, AttendanceCorrectionRequestDto.CorrectionDto dto, String loginId) {
        User processor = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        AttendanceLog log = attendanceLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("근태 로그를 찾을 수 없습니다."));
        AttendanceResult result = resultRepository.findByEmployeeAndWorkDate(log.getEmployee(), log.getWorkDate())
                .orElseThrow(() -> new EntityNotFoundException("정규직 기록을 찾을 수 없습니다."));

        LocalDateTime beforeTime = log.getLogTime();
        log.setLogTime(dto.getAfterTime());
        
        if ("IN".equals(log.getLogType())) {
            result.setCheckInTime(dto.getAfterTime());
        } else if ("OUT".equals(log.getLogType())) {
            result.setCheckOutTime(dto.getAfterTime());
        }

        // 이력 저장
        saveCorrectionRecord(result, null, log.getLogType(), beforeTime, dto.getAfterTime(), dto.getCorrectionReason(), processor);
    }

    // 근태 정정 이력 저장
    private void saveCorrectionRecord(
    		AttendanceResult result, AttendanceLogsDaily dailyLog, 
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