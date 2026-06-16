package com.hr24.work.meeting.dto;

import com.hr24.work.meeting.entity.ReservationParticipant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantResponse {

    private Long userId;
    private String userName;
    private int isOrganizer;

    public static ParticipantResponse from(ReservationParticipant participant) {
        return ParticipantResponse.builder()
                .userId(participant.getUser().getEmployeeId())
                .userName(participant.getUser().getName())
                .isOrganizer(participant.getIsOrganizer())
                .build();
    }
}
