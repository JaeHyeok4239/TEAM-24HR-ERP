package com.hr24.document.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceResultRepository;
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

	// Map을 Dto로 컨버팅
	private final ObjectMapper objectMapper;
	private final LeaveRepository leaveRepository;
	private final LeaveTypeRepository leaveTypeRepository;
	private final AttendanceResultRepository attendanceResultRepository;

	// 휴가 신청 데이터 생성 후 attendenceResult 생성
	@Transactional
	public void createLeaveFromContent(Document document) {
		HrRequestDto.LeaveDto dto;
		try {
			dto = objectMapper.convertValue(document.getDocumentContent(), HrRequestDto.LeaveDto.class);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("연차 정보 형식이 올바르지 않습니다.");
		}

		// 내용 검증은 추후 dto에 vaildation 추가

		LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveType())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 유형입니다"));

		Leave leave = Leave.builder().document(document).leaveType(leaveType).startDate(dto.getStartDate())
				.endDate(dto.getEndDate()).leaveCnt(dto.getLeaveCnt()).build();

		leaveRepository.save(leave);
		
		
	}

	// 근태 정정 데이터 생성 후 근태 정정 처리
	@Transactional
	public void createCorrectionFromContent(Document document) {
		
		HrRequestDto.AttendanceCorrectionDto dto;
		
		try {
			dto = objectMapper.convertValue(document.getDocumentContent(), HrRequestDto.AttendanceCorrectionDto.class);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("데이터 형식이 올바르지 않습니다.");
		}
		
		//근태 이력 데이터 불러오기
		AttendanceResult originResult = attendanceResultRepository
				.findByEmployeeAndWorkDate(document.getRequester(), dto.getTargetDate())
				.orElseThrow(() -> new EntityNotFoundException("해당 날짜에 근태 기록이 없습니다"));
		
		//근태 정정 이력 생성
		AttendanceCorrection correction = AttendanceCorrection
				.builder()
				.correctionTarget(originResult)
				.correctionType(dto.getCorrectionType())
				.correctionReason(dto.getCorrectionReason())
				.document(document)
				.beforeTime(dto.getBeforeTime())
				.afterTime(dto.getAfterTime())
				.processedBy(document.getProcessor())
				.build();
		
		
		//근태 이력 수정
		originResult.setAttendanceCorrection(correction);
		
		//출근 시각 정정 시
		if(correction.getCorrectionType() == "IN" && originResult.getCheckInTime().equals(correction.getBeforeTime())) {
			originResult.setCheckInTime(correction.getAfterTime());
		}
		
		//퇴근 시각 정정 시
		if(correction.getCorrectionType() == "OUT" && originResult.getCheckOutTime().equals(correction.getBeforeTime())) {
			originResult.setCheckOutTime(correction.getAfterTime());
		}
		
		//다 완료되면 정정 상태 업데이트
		originResult.setIsFixed("Y");
	}
}
