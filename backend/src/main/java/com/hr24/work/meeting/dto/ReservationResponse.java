package com.hr24.work.meeting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.hr24.work.meeting.entity.RoomReservation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponse {

    private Long reservationId;
    private Long roomId;
    private String roomName;
    private Long userId;
    private String userName;
    private String title;
    private LocalDate rsvDate;
    private String startTime;
    private String endTime;
    private String status;
    private String purpose;
    private LocalDateTime createAt;
    private List<ParticipantResponse> participants;

    public static ReservationResponse from(RoomReservation reservation, List<ParticipantResponse> participants) {
        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .roomId(reservation.getMeetingRoom().getRoomId())
                .roomName(reservation.getMeetingRoom().getRoomName())
                .userId(reservation.getUser().getEmployeeId())
                .userName(reservation.getUser().getName())
                .title(reservation.getTitle())
                .rsvDate(reservation.getRsvDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .purpose(reservation.getPurpose())
                .createAt(reservation.getCreateAt())
                .participants(participants)
                .build();
    }
}
