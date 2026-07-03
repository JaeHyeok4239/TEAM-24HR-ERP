package com.hr24.employee.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.EmployeeHistoryResponseDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.EmployeeHistory;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmployeeHistoryItem;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.EmployeeHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeHistoryService {

	private final EmployeeHistoryRepository employeeHistoryRepository;

	@Transactional
	public boolean saveEmploymentInfoHistories(
			User employee,
			User changedBy,
			Department newDepartment,
			Position newPosition,
			EmploymentType newEmploymentType,
			UserStatus newStatus,
			LocalDateTime newHireDate,
			LocalDateTime newResignationDate,
			String changeReason
	) {
		List<EmployeeHistory> histories = new ArrayList<>();

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.DEPARTMENT,
				getDepartmentName(employee.getDepartment()),
				getDepartmentName(newDepartment),
				changeReason
		);

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.POSITION,
				getPositionName(employee.getPosition()),
				getPositionName(newPosition),
				changeReason
		);

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.EMPLOYMENT_TYPE,
				getEnumName(employee.getEmploymentType()),
				getEnumName(newEmploymentType),
				changeReason
		);

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.STATUS,
				getEnumName(employee.getStatus()),
				getEnumName(newStatus),
				changeReason
		);

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.HIRE_DATE,
				formatDate(employee.getHireDate()),
				formatDate(newHireDate),
				changeReason
		);

		addHistoryIfChanged(
				histories,
				employee,
				changedBy,
				EmployeeHistoryItem.RESIGNATION_DATE,
				formatDate(employee.getResignationDate()),
				formatDate(newResignationDate),
				changeReason
		);

		if (histories.isEmpty()) {
			return false;
		}

		employeeHistoryRepository.saveAll(histories);

		return true;
	}
	
	@Transactional(readOnly = true)
	public List<EmployeeHistoryResponseDto> findHistories(Long employeeId) {
		return employeeHistoryRepository.findByEmployee_EmployeeIdOrderByCreatedAtDesc(employeeId)
				.stream()
				.map(EmployeeHistoryResponseDto::from)
				.toList();
	}

	private void addHistoryIfChanged(
			List<EmployeeHistory> histories,
			User employee,
			User changedBy,
			EmployeeHistoryItem changeItem,
			String beforeValue,
			String afterValue,
			String changeReason
	) {
		if (Objects.equals(beforeValue, afterValue)) {
			return;
		}

		histories.add(EmployeeHistory.create(
				employee,
				changedBy,
				changeItem,
				beforeValue,
				afterValue,
				changeReason
		));
	}

	private String getDepartmentName(Department department) {
		if (department == null) {
			return null;
		}

		return department.getDepartmentName();
	}

	private String getPositionName(Position position) {
		if (position == null) {
			return null;
		}

		return position.getPositionName();
	}

	private String getEnumName(Enum<?> value) {
		if (value == null) {
			return null;
		}

		return value.name();
	}

	private String formatDate(LocalDateTime value) {
		if (value == null) {
			return null;
		}

		return value.toLocalDate().toString();
	}
}