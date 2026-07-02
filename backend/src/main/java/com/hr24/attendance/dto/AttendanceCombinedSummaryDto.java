package com.hr24.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCombinedSummaryDto {
    private AttendanceSummaryDto regular;
    private AttendanceSummaryDto daily;
}