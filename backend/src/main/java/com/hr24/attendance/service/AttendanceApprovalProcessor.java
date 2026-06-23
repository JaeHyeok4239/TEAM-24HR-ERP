package com.hr24.attendance.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.employee.entity.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceApprovalProcessor { // 별도 클래스로 분리하면 관리하기 편해요
    
    private final AttendanceCorrectionRepository correctionRepository;
    private final AttendanceResultRepository resultRepository;
    private final AttendanceCalculator calculator;

    @Transactional
    public void executeCorrection(Long documentId) {
        // 결재 문서와 매핑된 정정 정보 가져오기
        AttendanceCorrection correction = correctionRepository.findByDocumentDocumentId(documentId)
                .orElseThrow(() -> new EntityNotFoundException("정정 정보가 없습니다."));

        // 이미 처리된 건인지 중복 체크(안전장치)
        if ("Y".equals(correction.getIsProcessed())) {
            return; // 이미 처리됨
        }

        // 근태 결과 업데이트
        AttendanceResult result = correction.getCorrectionTarget();
        result.setUpdatedAt(correction.getAfterTime()); // 수정 후 시간으로 변경
        
        User employee = result.getEmployee();
        String status = calculator.calculateStatus(employee, result, correction.getCorrectionType());
        // 근태 상태 재계산(Calculator 호출)
        result.setAttendanceStatus(status); 

        // result 업데이트 처리
        correction.setIsProcessed("Y");
        correction.setUpdatedAt(LocalDateTime.now());
    }
}