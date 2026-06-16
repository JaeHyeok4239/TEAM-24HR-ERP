package com.hr24.work.schedule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hr24.work.schedule.entity.Schedule;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponse {

    private Long scheduleId;
    private Long userId;
    private String userName;
    private Long deptId;
    private String deptName;
    private String title;
    private String scheduleType;
    private LocalDate startDt;
    private LocalDate endDt;
    private String location;
    private String memo;
    private LocalDateTime createdAt;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .userId(schedule.getUser().getEmployeeId())
                .userName(schedule.getUser().getName())
                .deptId(schedule.getDepartment() != null ? schedule.getDepartment().getDepartmentId() : null)
                .deptName(schedule.getDepartment() != null ? schedule.getDepartment().getDepartmentName() : null)
                .title(schedule.getTitle())
                .scheduleType(schedule.getScheduleType())
                .startDt(schedule.getStartDt())
                .endDt(schedule.getEndDt())
                .location(schedule.getLocation())
                .memo(schedule.getMemo())
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}
