package com.hr24.attendance.service;

import com.hr24.document.repository.LeaveDateRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hr24.attendance.entity.AnnualLeaveBalances;
import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.enums.AttendanceStatus;
import com.hr24.attendance.repository.AnnualLeaveBalancesRepository;
import com.hr24.attendance.repository.AttendanceCorrectionRepository;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.document.dto.DocumentDetailDto;
import com.hr24.document.entity.Document;
import com.hr24.document.entity.Leave;
import com.hr24.document.entity.LeaveDate;
import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.DocumentRepository;
import com.hr24.document.repository.LeaveRepository;
import com.hr24.document.repository.LeaveTypeRepository;
import com.hr24.document.service.DocumentProcessService;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//문서로 요청한 근태 관련 업무 처리 서비스 
public class AttendanceProcessService {

	private final LeaveDateRepository leaveDateRepository;
	private final AttendanceCalculator calculator;
	// 문서 처리 service에서 데이터 변환
	private final DocumentProcessService documentProcessService;
	private final AttendanceResultRepository attendanceResultRepository;
	private final LeaveTypeRepository leaveTypeRepository;
	private final LeaveRepository leaveRepository;
	private final AnnualLeaveBalancesRepository annualLeaveRepository;
	private final DocumentRepository documentRepository;
	private final AttendanceCorrectionRepository attendanceCorrectionRepository;
	

	// 휴가 데이터 반영
	@Transactional
	public void createLeave(Long documentId, User requester) {

	    DocumentDetailDto.LeaveRequestDto data = documentProcessService.executeLeave(documentId);

	    Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

	    LeaveType leaveType = leaveTypeRepository.findById(data.getLeaveType())
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 휴가 유형입니다"));

	    // 유급휴가면 연차 잔액 먼저 검증
	    if (leaveType.getIsPaid().equals("Y")) {
	        Integer currentYear = LocalDate.now().getYear();

	        AnnualLeaveBalances leaveBalances = annualLeaveRepository.findByEmployeeAndLeaveYear(requester, currentYear)
	                .orElseThrow(() -> new EntityNotFoundException("연차 정보를 찾을 수 없습니다"));

	        BigDecimal beforeRemainingDays = leaveBalances.getRemainingDays();

	        if (beforeRemainingDays.subtract(data.getLeaveCnt()).compareTo(BigDecimal.ZERO) < 0) {
	            throw new IllegalArgumentException("잔여 연차가 부족합니다.");
	        }

	        leaveBalances.setRemainingDays(beforeRemainingDays.subtract(data.getLeaveCnt()));
	    }

	    // Leave 저장
	    Leave leave = Leave.builder()
	            .document(document)
	            .leaveType(leaveType)
	            .leaveCnt(data.getLeaveCnt())
	            .leaveReason(data.getLeaveReason())
	            .build();

	    leaveRepository.save(leave);

	    // LeaveDate 저장
	    List<LeaveDate> leaveDates = data.getLeaveDates().stream()
	            .map(date -> LeaveDate.builder()
	                    .leave(leave)
	                    .leaveDate(date)
	                    .build())
	            .collect(Collectors.toList());

	    leaveDateRepository.saveAll(leaveDates);
	    
	    //근태 이력 생성
	    List<AttendanceResult> results = data.getLeaveDates().stream()
	            .map(date -> AttendanceResult.builder()
	                    .employee(requester)
	                    .workDate(date)
	                    .attendanceStatus(AttendanceStatus.LEAVE)
	                    .leave(leave)
	                    .isHolidayWork("N")
	                    .isMissingCheckout("N")
	                    .isFixed("N")
	                    .createdAt(LocalDateTime.now())
	                    .build())
	            .collect(Collectors.toList());

	    attendanceResultRepository.saveAll(results);
	}

	// 데이터 이상 없으면 그대로 정정 처리
	@Transactional
	public void createCorrection(Long documentId, User currentUser) {

		// 문서에 담겨있는 데이터 가져오기(사용자가 보낸 dto)
		DocumentDetailDto.AttendanceCorrectionRequestDto data = documentProcessService.executeCorrection(documentId);
		
		//문서 찾기
		Document document = documentRepository.findById(documentId)
	            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));

		
		AttendanceResult result = attendanceResultRepository.findById(data.getCorrectionTarget())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 근태 이력입니다"));

		AttendanceCorrection correction = AttendanceCorrection.builder()
				.beforeTime(data.getBeforeTime())
				.afterTime(data.getAfterTime())
				.correctionType(data.getCorrectionType())
				.correctionReason(data.getCorrectionReason())
				.correctionTarget(result)
				.document(document).isProcessed("Y").processedBy(currentUser).build();
		
		// 근태 결과 업데이트
		if(correction.getCorrectionType().equals("IN")) {
			result.setCheckInTime(correction.getAfterTime());
		}
		else {
			result.setCheckOutTime(correction.getAfterTime());
		}

		User employee = result.getEmployee();
		
		String status = calculator.calculateStatus(employee, result, correction.getCorrectionType());
		
		// 근태 상태 재계산(Calculator 호출)
		result.setAttendanceStatus(AttendanceStatus.valueOf(status));

		// result 업데이트 처리
		correction.setIsProcessed("Y");
		correction.setUpdatedAt(LocalDateTime.now());
		
		attendanceCorrectionRepository.save(correction);
		
		result.setIsFixed("Y");
		result.setUpdatedAt(LocalDateTime.now()); //수정 시각 : 현재

	}

}