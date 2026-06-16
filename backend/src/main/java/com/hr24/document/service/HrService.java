package com.hr24.document.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.document.dto.HrRequestDto;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.Leave;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.document.repository.LeaveTypeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HrService {
	
	//Map을 Dto로 컨버팅
	private final ObjectMapper objectMapper;
	private final LeaveRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    
    //휴가 신청 처리
    @Transactional
    public void createLeaveFromContent(Document document, Map<String, Object> content) {
        HrRequestDto.LeaveDto dto;
        try {
            dto = objectMapper.convertValue(content, HrRequestDto.LeaveDto.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("연차 정보 형식이 올바르지 않습니다.");
        }

        if (dto.getLeaveType() == null || dto.getStartDate() == null
                || dto.getEndDate() == null || dto.getLeaveCnt() == null) {
            throw new IllegalArgumentException("연차 정보가 필요합니다.");
        }

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveType())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 유형입니다"));

        Leave leave = Leave.builder()
                .document(document)
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .leaveCnt(dto.getLeaveCnt())
                .build();

        leaveRepository.save(leave);
    }
}
