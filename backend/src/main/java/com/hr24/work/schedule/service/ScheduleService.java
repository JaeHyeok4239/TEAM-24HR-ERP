package com.hr24.work.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.work.schedule.dto.HolidayResponse;
import com.hr24.work.schedule.dto.ScheduleRequest;
import com.hr24.work.schedule.dto.ScheduleResponse;
import com.hr24.work.schedule.entity.Schedule;
import com.hr24.work.schedule.repository.HolidayRepository;
import com.hr24.work.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    // 기간 내 일정 조회 - 관리자는 전체, 일반 직원은 본인 관련 일정만
    public List<ScheduleResponse> getSchedules(LocalDate startDt, LocalDate endDt) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginId = auth.getName();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return scheduleRepository.findAllInRange(startDt, endDt).stream()
                    .map(ScheduleResponse::from)
                    .collect(Collectors.toList());
        }

        Long deptId = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;

        return scheduleRepository.findSchedulesForUser(
                startDt, endDt,
                user.getEmployeeId(),
                deptId
        ).stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // 연도별 공휴일 목록 조회
    public List<HolidayResponse> getHolidays(Integer year) {
        return holidayRepository.findByHolidayYear(year).stream()
                .map(HolidayResponse::from)
                .collect(Collectors.toList());
    }

    // 일정 등록 - DEPT 타입일 때만 부서 연결
    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Department department = null;
        if ("DEPT".equals(request.getScheduleType()) && request.getDeptId() != null) {
            department = departmentRepository.findById(request.getDeptId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));
        }

        Schedule schedule = Schedule.builder()
                .user(user)
                .department(department)
                .title(request.getTitle())
                .scheduleType(request.getScheduleType())
                .startDt(request.getStartDt())
                .endDt(request.getEndDt())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .memo(request.getMemo())
                .createdAt(LocalDateTime.now())
                .build();

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    // 일정 수정
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        schedule.setTitle(request.getTitle());
        schedule.setStartDt(request.getStartDt());
        schedule.setEndDt(request.getEndDt());
        schedule.setLocation(request.getLocation());
        schedule.setMemo(request.getMemo());

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        scheduleRepository.delete(schedule);
    }
}
