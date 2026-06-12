package com.hr24.work.meeting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.work.meeting.dto.MeetingRoomResponse;
import com.hr24.work.meeting.dto.ParticipantResponse;
import com.hr24.work.meeting.dto.ReservationRequest;
import com.hr24.work.meeting.dto.ReservationResponse;
import com.hr24.work.meeting.entity.MeetingRoom;
import com.hr24.work.meeting.entity.ReservationParticipant;
import com.hr24.work.meeting.entity.RoomReservation;
import com.hr24.work.meeting.repository.MeetingRoomRepository;
import com.hr24.work.meeting.repository.ReservationParticipantRepository;
import com.hr24.work.meeting.repository.RoomReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final RoomReservationRepository reservationRepository;
    private final ReservationParticipantRepository participantRepository;
    private final UserRepository userRepository;

    // ACTIVE 상태인 회의실 목록만 반환
    public List<MeetingRoomResponse> getActiveRooms() {
        return meetingRoomRepository.findByStatus("ACTIVE").stream()
                .map(MeetingRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 날짜의 전체 예약 목록과 참석자 정보를 함께 반환
    public List<ReservationResponse> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByRsvDate(date).stream()
                .map(r -> ReservationResponse.from(r, getParticipants(r)))
                .collect(Collectors.toList());
    }

    // 로그인한 사용자의 예약 목록 반환
    public List<ReservationResponse> getMyReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        return reservationRepository.findByUser(user).stream()
                .map(r -> ReservationResponse.from(r, getParticipants(r)))
                .collect(Collectors.toList());
    }

    // 예약 생성 - 시간 중복 체크 후 예약 및 참석자 저장
    @Transactional
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        MeetingRoom room = meetingRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회의실입니다."));

        // 같은 날짜, 같은 회의실의 기존 예약과 시간이 겹치는지 확인
        boolean isOverlap = reservationRepository
                .findConfirmedByRoomAndDate(room, request.getRsvDate())
                .stream()
                .anyMatch(r ->
                        request.getStartTime().compareTo(r.getEndTime()) < 0 &&
                        request.getEndTime().compareTo(r.getStartTime()) > 0
                );

        if (isOverlap) {
            throw new RuntimeException("해당 시간에 이미 예약이 존재합니다.");
        }

        // 예약 저장
        RoomReservation reservation = RoomReservation.builder()
                .meetingRoom(room)
                .user(user)
                .title(request.getTitle())
                .rsvDate(request.getRsvDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("CONFIRMED")
                .purpose(request.getPurpose())
                .createAt(LocalDateTime.now())
                .build();

        reservationRepository.save(reservation);

        // 참석자 저장 - 예약자 본인은 주최자(1), 나머지는 참석자(0)로 구분
        if (request.getParticipantIds() != null) {
            for (Long participantId : request.getParticipantIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 참석자입니다."));

                participantRepository.save(ReservationParticipant.builder()
                        .reservation(reservation)
                        .user(participant)
                        .isOrganizer(participantId.equals(userId) ? 1 : 0)
                        .build());
            }
        }

        return ReservationResponse.from(reservation, getParticipants(reservation));
    }

    // 예약 상태를 CANCELLED로 변경하여 취소 처리
    @Transactional
    public void cancelReservation(Long reservationId) {
        RoomReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    // 예약에 대한 참석자 목록을 ParticipantResponse로 변환하는 공통 메서드
    private List<ParticipantResponse> getParticipants(RoomReservation reservation) {
        return participantRepository.findByReservation(reservation).stream()
                .map(ParticipantResponse::from)
                .collect(Collectors.toList());
    }
}
