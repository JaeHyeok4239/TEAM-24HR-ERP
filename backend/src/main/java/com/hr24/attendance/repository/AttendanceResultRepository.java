package com.hr24.attendance.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.attendance.entity.AttendanceLog;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.entity.Workplace;
import com.hr24.attendance.enums.AttendanceStatus;
import com.hr24.employee.entity.User;
import com.hr24.employee.enums.UserStatus;

public interface AttendanceResultRepository extends JpaRepository<AttendanceResult, Long>{
	
	// 스케줄러
	boolean existsByWorkDate(LocalDate workDate);
	
	// 한 명 조회
	Optional<AttendanceResult> findByEmployeeAndWorkDate(User employee, LocalDate workDate);
	
	// 특정 날짜, 특정 근태 상태의 직원 목록 조회
	@Query("SELECT ar FROM AttendanceResult ar JOIN FETCH ar.employee " +
	       "WHERE ar.workDate = :workDate " +
	       "AND ar.attendanceStatus = :status")
	List<AttendanceResult> findByWorkDateAndAttendanceStatus(
	    @Param("workDate") LocalDate workDate, 
	    @Param("status") AttendanceStatus status
	);	

	// 특정 직원의 특정 날짜 근태 정보+그 직원의 상세 정보
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	           "where ar.employee.employeeId = :employeeId " +
	           "and ar.workDate = :workDate")
	    Optional<AttendanceResult> findByEmployeeIdWithUser(
	        @Param("employeeId") Long employeeId, 
	        @Param("workDate") LocalDate workDate
	    );
	
	// 특정 기간 직원 한 명의 근태 조회
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	           "where ar.employee = :employee " +
	           "and ar.workDate between :start and :end")
	    List<AttendanceResult> findByEmployeeWithUser(
	        @Param("employee") User employee, 
	        @Param("start") LocalDate start, 
	        @Param("end") LocalDate end
	    );
	
	// 특정 기간 모든 직원의 근태 조회
	@Query("select ar from AttendanceResult ar join fetch ar.employee " +
	       "where ar.workDate between :start and :end")
	List<AttendanceResult> findAllWithEmployeeByWorkDateBetween(
	    @Param("start") LocalDate start, 
	    @Param("end") LocalDate end
	);
	
	// 중복 막기(직원별, 날짜별 존재 여부 확인)
	boolean existsByEmployeeAndWorkDate(User employee, LocalDate workDate);


	@Query("SELECT ar FROM AttendanceResult ar WHERE ar.employee.employeeId = :employeeId AND ar.workDate = :workDate")
	Optional<AttendanceResult> findByEmployeeIdAndWorkDate(@Param("employeeId") Long employeeId, @Param("workDate") LocalDate workDate);


	List<AttendanceResult> findAllByWorkDate(LocalDate todayDate);
	
	// 특정 직원의 특정 월 근태 기록 뽑아오기
	@Query("SELECT a FROM AttendanceResult a " +
		       "WHERE a.employee.employeeId = :employeeId " +
		       "AND FUNCTION('TO_CHAR', a.workDate, 'YYYY-MM') = :yearMonth")
		List<AttendanceResult> findByEmployeeIdAndMonth(@Param("employeeId") Long employeeId, 
		                                                @Param("yearMonth") String yearMonth);
	
	// 시작일과 종료일 사이의 근태 조회 
	List<AttendanceResult> findByEmployeeEmployeeIdAndWorkDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

	@Modifying
	@Query("UPDATE AttendanceResult ar SET ar.isCheckoutMissing = 'Y', ar.updatedAt = :now WHERE ar.workDate = :date AND ar.attendanceStatus IN :targetStatuses AND ar.checkOutTime IS NULL")
	int updateIsCheckoutMissing(@Param("date") LocalDate date,
	                            @Param("targetStatuses") List<AttendanceStatus> targetStatuses,
	                            @Param("now") LocalDateTime now);

}
