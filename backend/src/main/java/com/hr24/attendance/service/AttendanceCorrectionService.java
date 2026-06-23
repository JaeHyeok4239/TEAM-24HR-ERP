package com.hr24.attendance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.attendance.dto.DailyCorrectionDto;
import com.hr24.attendance.dto.RegularCorrectionDto;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.enums.AttendanceStatus;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceLogRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.document.entity.Document;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
// 정정 - 누가 신청하든 상관 없는 범용 로직
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository attendanceCorrectionRepository;
    private final AttendanceResultRepository attendanceResultRepository;
    private final AttendanceCalculator attendanceCalculator;
    private final AttendanceLogRepository attendanceLogRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository; 
    private final DocumentTypeRepository documentTypeRepository;

    // 정규직 정정 신청 시
    public void correctRegular(Long employeeId, Long resultId, RegularCorrectionDto dto, Long documentId) {
        AttendanceResult result = attendanceResultRepository.findById(resultId)
                .orElseThrow(() -> new EntityNotFoundException("근태 기록을 찾을 수 없습니다."));

        Document document = documentRepository.findById(documentId)
        		.orElseThrow(() -> new EntityNotFoundException("문서를 찾을 수 업습니다."));
        
        // 정정 이력 엔티티 생성
        AttendanceCorrection correction = AttendanceCorrection.builder()
                .correctionTarget(result)
                .document(document)
                .correctionType(dto.getCorrectionType()) // IN 또는 OUT
                .beforeTime("IN".equals(dto.getCorrectionType()) ? result.getCheckInTime() : result.getCheckOutTime()) 	// 수정 전 시간
                .afterTime(dto.getAfterTime())       	// 수정 후 시간
                .correctionReason(dto.getReason())   	// 사유 100자
                .isProcessed("N")                    	// 아직 승인 전이므로 N
                .build();

        attendanceCorrectionRepository.save(correction);
        log.info("정정 요청이 생성되었습니다. DocumentID: {}", document.getDocumentId());
    }
    
    // 일용직 수정 시
    @Transactional
    public void correctDaily(Long logId, DailyCorrectionDto dto) {
        // 수정할 로그 찾음(IN인지 OUT인지 결정)
        AttendanceLog log = attendanceLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("근태 기록을 찾을 수 없습니다."));

        // 현 시간을 beforeTime에 넣음
        LocalDateTime beforeTime = log.getLogTime();

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
                .build();
                
        attendanceCorrectionRepository.save(correction);
    }

    // 정정 처리(전자결재 승인 시 호출)
    public void process(Document document) {
        // 해당 문서에 연결된 정정 이력 조회
        AttendanceCorrection correction = attendanceCorrectionRepository.findByDocument(document)
                .orElseThrow(() -> new EntityNotFoundException("해당 문서와 연결된 정정 이력이 없습니다."));

        // 중복 처리 방지(이미 Y이면 무시)
        if ("Y".equals(correction.getIsProcessed())) {
            log.warn("이미 처리된 정정 건입니다. DocumentID: {}", document.getDocumentId());
            return;
        }

        // AttendanceResult 업데이트
        AttendanceResult result = correction.getCorrectionTarget();
        
        // 정정 종류에 따른 시간 업데이트
        if ("IN".equals(correction.getCorrectionType())) {
            result.setCheckInTime(correction.getAfterTime());
        } else {
            result.setCheckOutTime(correction.getAfterTime());
        }
        
        User employee = result.getEmployee();

        // 상태 재계산
        // Calculator가 반환하는 상태값으로 업데이트
        String newStatus = attendanceCalculator.calculateStatus(employee, result, correction.getCorrectionType()); 
        result.setAttendanceStatus(AttendanceStatus.valueOf(newStatus));

        // 정정 처리 완료 표시
        correction.setIsProcessed("Y");
        
        log.info("근태 정정 처리가 완료되었습니다. ResultID: {}", result.getAttendanceResultId());
    }
}