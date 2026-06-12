package com.hr24.work.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.work.meeting.entity.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    List<MeetingRoom> findByStatus(String status);
}
