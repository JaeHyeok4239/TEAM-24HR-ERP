package com.hr24.work.schedule.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.work.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 관리자용 - 기간 내 전체 일정 조회
    @Query("SELECT s FROM Schedule s WHERE s.startDt <= :endDt AND s.endDt >= :startDt")
    List<Schedule> findAllInRange(
        @Param("startDt") LocalDate startDt,
        @Param("endDt") LocalDate endDt
    );

    // 일반 직원용 - 내 개인일정 + 내 부서일정 + 회사일정
    @Query("""
        SELECT s FROM Schedule s
        WHERE s.startDt <= :endDt AND s.endDt >= :startDt
        AND (
            (s.scheduleType = 'PERSONAL' AND s.user.employeeId = :userId)
            OR (s.scheduleType = 'DEPT' AND s.department.departmentId = :deptId)
            OR s.scheduleType = 'COMPANY'
        )
    """)
    List<Schedule> findSchedulesForUser(
        @Param("startDt") LocalDate startDt,
        @Param("endDt") LocalDate endDt,
        @Param("userId") Long userId,
        @Param("deptId") Long deptId
    );
}
