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
import com.hr24.work.notification.dto.NotificationMessage;
import com.hr24.work.notification.service.NotificationService;
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
    private final NotificationService notificationService;

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
    public ScheduleResponse createSchedule(String loginId, ScheduleRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // COMPANY 일정은 ADMIN만 등록 가능
        if ("COMPANY".equals(request.getScheduleType()) && !isAdmin) {
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Department department = null;
        if ("DEPT".equals(request.getScheduleType())) {
            if (request.getDeptId() != null) {
                department = departmentRepository.findById(request.getDeptId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));
                // DEPT 일정은 본인 부서만 등록 가능 (ADMIN 제외)
                if (!isAdmin && (user.getDepartment() == null ||
                        !user.getDepartment().getDepartmentId().equals(department.getDepartmentId()))) {
                    throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
                }
            } else if (user.getDepartment() != null) {
                department = user.getDepartment();
            }
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

        ScheduleResponse response = ScheduleResponse.from(scheduleRepository.save(schedule));

        // DEPT/COMPANY 일정 등록 시 알림 발송 (개인 일정은 알림 없음)
        if ("COMPANY".equals(request.getScheduleType())) {
            notificationService.sendCompanyNotification(new NotificationMessage(
                "SCHEDULE_COMPANY",
                "전사 일정 등록",
                user.getName() + "님이 전사 일정을 등록했습니다: " + request.getTitle()
            ));
        } else if ("DEPT".equals(request.getScheduleType()) && department != null) {
            notificationService.sendDeptNotification(
                department.getDepartmentName(),
                new NotificationMessage(
                    "SCHEDULE_DEPT",
                    "부서 일정 등록",
                    user.getName() + "님이 부서 일정을 등록했습니다: " + request.getTitle()
                )
            );
        }

        return response;
    }

    // 일정 수정 - 작성자 본인 또는 ADMIN만 가능
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest request, String loginId, boolean isAdmin) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        boolean isOwner = schedule.getUser().getLoginId().equals(loginId);
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("본인이 등록한 일정만 수정할 수 있습니다.");
        }

        schedule.setTitle(request.getTitle());
        schedule.setStartDt(request.getStartDt());
        schedule.setEndDt(request.getEndDt());
        schedule.setLocation(request.getLocation());
        schedule.setMemo(request.getMemo());

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    // 일정 삭제 - 작성자 본인 또는 ADMIN만 가능
    @Transactional
    public void deleteSchedule(Long scheduleId, String loginId, boolean isAdmin) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        boolean isOwner = schedule.getUser().getLoginId().equals(loginId);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("본인이 등록한 일정만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
