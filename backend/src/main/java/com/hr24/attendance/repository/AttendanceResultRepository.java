package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;

public interface AttendanceResultRepository extends JpaRepository<AttendanceResult, Long>{
	//직원ID와 근무 날짜로 근태 결과 있는지 찾기
	Optional<AttendanceResult> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
	//한 달치 데이터 긁어오기
	//Optional은 데이터 1개만 나올 때 쓰기에 List 사용
	List<AttendanceResult> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate startDate, LocalDate endData);
}
