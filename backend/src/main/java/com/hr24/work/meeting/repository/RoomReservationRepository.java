package com.hr24.work.meeting.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.employee.entity.User;
import com.hr24.work.meeting.entity.MeetingRoom;
import com.hr24.work.meeting.entity.RoomReservation;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {

    List<RoomReservation> findByMeetingRoomAndRsvDateAndStatusNot(MeetingRoom meetingRoom, LocalDate rsvDate, String status);

    List<RoomReservation> findByUser(User user);

    List<RoomReservation> findByRsvDate(LocalDate rsvDate);
}
