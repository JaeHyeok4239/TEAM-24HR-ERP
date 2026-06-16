package com.hr24.work.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.work.meeting.entity.ReservationParticipant;
import com.hr24.work.meeting.entity.RoomReservation;

public interface ReservationParticipantRepository extends JpaRepository<ReservationParticipant, Long> {

    List<ReservationParticipant> findByReservation(RoomReservation reservation);

    void deleteByReservation(RoomReservation reservation);
}
