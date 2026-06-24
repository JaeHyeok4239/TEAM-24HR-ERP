package com.hr24.document.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.attendance.service.AttendanceCorrectionService;
import com.hr24.document.dto.DocumentResponseDto;
import com.hr24.document.dto.HrRequestDto;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.DocumentProcess;
import com.hr24.document.entity.DocumentType;
import com.hr24.document.entity.Leave;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.DocumentProcessRepository;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.document.repository.DocumentTypeRepository;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.document.repository.LeaveTypeRepository;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//업무 처리 관련 서비스
public class DocumentProcessService {

	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final LeaveRepository leaveRepository;
	private final LeaveTypeRepository leaveTypeRepository;
	private final AttendanceResultRepository attendanceResultRepository;
	
	// 업무처리함 조회 (내가 처리해야 할 문서 목록)
	public Page<DocumentResponseDto.DocumentListDto> myProcessBox(String loginId, Pageable pageable) {
		User user = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다"));

		Department department = user.getDepartment();

		return documentRepository.findProcessableDocList(department, pageable)
				.map(DocumentResponseDto.DocumentListDto::from);
	}

	// 분기 처리 boolean 메소드
	@Transactional
	public boolean isRequiredProcessing(Long documentId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		return document.getDocumentType().getRequiredProcessing().equals("Y");
	}

	// 휴가 신청 데이터 생성
	@Transactional(readOnly = true)
	public HrRequestDto.LeaveDto executeLeave(Long documentId) {

	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

	    HrRequestDto.LeaveDto dto;

	    try {
	        dto = objectMapper.convertValue(document.getDocumentContent(), HrRequestDto.LeaveDto.class);
	    } catch (IllegalArgumentException e) {
	        throw new IllegalArgumentException("연차 정보 형식이 올바르지 않습니다.");
	    }

	    LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveType())
	            .orElseThrow(() -> new EntityNotFoundException("휴가 유형이 존재하지 않습니다"));

	    BigDecimal leaveCnt;

	    if (leaveType.getIsPaid().equals("N")) {
	        leaveCnt = BigDecimal.ZERO;
	    } else if (leaveType.getTypeName().equals("반차")) {
	        leaveCnt = BigDecimal.valueOf(0.5);
	    } else {
	        leaveCnt = BigDecimal.valueOf(dto.getLeaveDates().size()); // 날짜 리스트 개수로 계산
	    }

	    dto.setLeaveCnt(leaveCnt);

	    return dto;
	}

	// 근태 정정 데이터 생성
	@Transactional
	public HrRequestDto.AttendanceCorrectionDto executeCorrection(Long documentId) {

		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		HrRequestDto.AttendanceCorrectionDto dto;

		try {
			dto = objectMapper.convertValue(document.getDocumentContent(), HrRequestDto.AttendanceCorrectionDto.class);

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("데이터 형식이 올바르지 않습니다.");
		}

		return dto;
	}

	// 문서 상세 처리 화면용 조회 (결재이력 + detail 데이터 포함)
//    public DocumentResponseDto.DocumentDto viewForProcess(Long documentId) {
//        // viewDocument와 유사하지만 처리자 권한 체크 포함
//        ...
//    }
}
