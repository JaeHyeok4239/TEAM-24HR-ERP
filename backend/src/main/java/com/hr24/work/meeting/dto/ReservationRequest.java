package com.hr24.work.meeting.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {

    private Long roomId;
    private String title;
    private LocalDate rsvDate;
    private String startTime;
    private String endTime;
    private String purpose;
    private List<Long> participantIds;
}
