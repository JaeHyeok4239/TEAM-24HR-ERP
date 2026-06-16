package com.hr24.work.schedule.dto;

import java.time.LocalDate;

import com.hr24.work.schedule.entity.Holiday;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidayResponse {

    private Long holidayId;
    private Integer holidayYear;
    private LocalDate holidayDate;
    private String holidayName;
    private int isSubstitute;

    public static HolidayResponse from(Holiday holiday) {
        return HolidayResponse.builder()
                .holidayId(holiday.getHolidayId())
                .holidayYear(holiday.getHolidayYear())
                .holidayDate(holiday.getHolidayDate())
                .holidayName(holiday.getHolidayName())
                .isSubstitute(holiday.getIsSubstitute())
                .build();
    }
}
