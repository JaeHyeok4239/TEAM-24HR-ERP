package com.hr24.work.meeting.dto;

import com.hr24.work.meeting.entity.MeetingRoom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingRoomResponse {

    private Long roomId;
    private String roomName;
    private Integer capacity;
    private String location;
    private String status;

    public static MeetingRoomResponse from(MeetingRoom meetingRoom) {
        return MeetingRoomResponse.builder()
                .roomId(meetingRoom.getRoomId())
                .roomName(meetingRoom.getRoomName())
                .capacity(meetingRoom.getCapacity())
                .location(meetingRoom.getLocation())
                .status(meetingRoom.getStatus())
                .build();
    }
}
