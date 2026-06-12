package com.hr24.work.meeting.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.employee.entity.User;
import com.hr24.work.meeting.entity.MeetingRoom;
import com.hr24.work.meeting.entity.RoomReservation;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {

    // 특정 회의실 + 날짜의 취소되지 않은 예약 조회 (시간 중복 체크용)
    @Query("SELECT r FROM RoomReservation r WHERE r.meetingRoom = :room AND r.rsvDate = :date AND r.status != 'CANCELLED'")
    List<RoomReservation> findConfirmedByRoomAndDate(@Param("room") MeetingRoom room, @Param("date") LocalDate date);

    // 특정 사용자의 예약 목록 조회
    List<RoomReservation> findByUser(User user);

    // 특정 날짜의 전체 예약 목록 조회
    List<RoomReservation> findByRsvDate(LocalDate rsvDate);
}
