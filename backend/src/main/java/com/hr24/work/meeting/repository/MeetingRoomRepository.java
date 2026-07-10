package com.hr24.work.meeting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.hr24.work.meeting.entity.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    List<MeetingRoom> findByStatus(String status);

    // 비관적 락 - 예약 생성 시 같은 회의실에 대한 동시 요청을 직렬화 (더블부킹 방지)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM MeetingRoom r WHERE r.roomId = :roomId")
    Optional<MeetingRoom> findByIdForUpdate(@Param("roomId") Long roomId);
}
