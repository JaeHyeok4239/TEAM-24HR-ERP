package com.hr24.document.service;

import org.springframework.stereotype.Service;

import com.hr24.document.entity.LeaveType;
import com.hr24.document.repository.LeaveTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

	private final LeaveTypeRepository leaveTypeRepository;
	
	public LeaveType createLeaveType(LeaveType leaveType) {
		
		LeaveType saved = leaveTypeRepository.save(leaveType);
		
		return saved;
	}
}
