package com.hr24.work.schedule.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleRequest {

    private Long deptId;
    private String title;
    private String scheduleType;
    private LocalDate startDt;
    private LocalDate endDt;
    private String location;
    private String memo;
}
