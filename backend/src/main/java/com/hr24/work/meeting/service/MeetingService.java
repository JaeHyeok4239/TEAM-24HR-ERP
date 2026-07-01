package com.hr24.work.meeting.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.DepartmentRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.work.meeting.dto.DepartmentSimpleResponse;
import com.hr24.work.meeting.dto.EmployeeSimpleResponse;
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
import com.hr24.work.notification.dto.NotificationMessage;
import com.hr24.work.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {

    // 팀장 회의(MANAGER) 등록 가능한 최소 직급 sortOrder - 차장(5) 이상
    private static final int MIN_MANAGER_MEETING_SORT_ORDER = 5;

    private final MeetingRoomRepository meetingRoomRepository;
    private final RoomReservationRepository reservationRepository;
    private final ReservationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;

    // 부서 전체 목록 반환 (회의 초대 대상 선택용)
    public List<DepartmentSimpleResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentSimpleResponse::from)
                .collect(Collectors.toList());
    }

    // ACTIVE 상태인 직원 전체 목록 반환 (회의 참석자 선택용)
    public List<EmployeeSimpleResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .map(EmployeeSimpleResponse::from)
                .collect(Collectors.toList());
    }

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
    public List<ReservationResponse> getMyReservations(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        return reservationRepository.findByUser(user).stream()
                .map(r -> ReservationResponse.from(r, getParticipants(r)))
                .collect(Collectors.toList());
    }

    // 예약 생성 - 시간 중복 체크 후 예약 및 참석자 저장
    @Transactional
    public ReservationResponse createReservation(String loginId, boolean isAdmin, ReservationRequest request) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 팀장 회의는 차장 이상만 등록 가능
        if ("MANAGER".equals(request.getMeetingType())) {
            Integer sortOrder = user.getPosition() != null ? user.getPosition().getSortOrder() : null;
            if (sortOrder == null || sortOrder < MIN_MANAGER_MEETING_SORT_ORDER) {
                throw new BusinessException(ErrorCode.MEETING_ACCESS_DENIED);
            }
        }

        // 전사 회의는 ADMIN만 등록 가능
        if ("COMPANY".equals(request.getMeetingType()) && !isAdmin) {
            throw new BusinessException(ErrorCode.MEETING_COMPANY_ACCESS_DENIED);
        }

        MeetingRoom room = meetingRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회의실입니다."));

        // 비관적 락으로 조회 - 트랜잭션 종료 전까지 동일 회의실/날짜 접근 차단
        boolean isOverlap = reservationRepository
                .findConfirmedByRoomAndDateWithLock(room, request.getRsvDate())
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
                .meetingType(request.getMeetingType())
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
                        .isOrganizer(participantId.equals(user.getEmployeeId()) ? 1 : 0)
                        .build());
            }
        }

        // 회의 유형별 알림 발송 (실패해도 예약은 유지)
        try {
            String meetingType = request.getMeetingType();
            String timeInfo = request.getRsvDate() + " " + request.getStartTime() + "~" + request.getEndTime();

            if ("COMPANY".equals(meetingType)) {
                notificationService.sendCompanyNotification(new NotificationMessage(
                    "MEETING_COMPANY",
                    "전사 회의 예약",
                    user.getName() + "님이 전사 회의를 예약했습니다: " + request.getTitle() + " (" + timeInfo + ")"
                ));
            } else if ("MANAGER".equals(meetingType)) {
                userRepository.findActiveManagersByMinSortOrder(6, UserStatus.ACTIVE)
                    .forEach(manager -> notificationService.sendPersonalNotification(
                        manager.getLoginId(),
                        new NotificationMessage(
                            "MEETING_MANAGER",
                            "팀장 회의 예약",
                            user.getName() + "님이 팀장 회의를 예약했습니다: " + request.getTitle() + " (" + timeInfo + ")"
                        )
                    ));
            } else if ("DEPT".equals(meetingType) && request.getInvitedDeptIds() != null) {
                request.getInvitedDeptIds().forEach(deptId ->
                    departmentRepository.findById(deptId).ifPresent(dept ->
                        notificationService.sendDeptNotification(dept.getDepartmentName(),
                            new NotificationMessage(
                                "MEETING_DEPT",
                                "부서 간 회의 요청",
                                user.getName() + "님이 회의를 요청했습니다: " + request.getTitle() + " (" + timeInfo + ")"
                            ))
                    )
                );
            }

            // 예약자 본인 확인 알림
            notificationService.sendPersonalNotification(loginId, new NotificationMessage(
                "MEETING_RESERVATION",
                "회의실 예약 완료",
                room.getRoomName() + " 예약이 완료되었습니다 (" + request.getStartTime() + "~" + request.getEndTime() + ")"
            ));

            // 초대된 참석자 개인 알림 (예약자 본인 제외)
            if (request.getParticipantIds() != null) {
                request.getParticipantIds().stream()
                        .filter(id -> !id.equals(user.getEmployeeId()))
                        .forEach(id -> userRepository.findById(id).ifPresent(participant ->
                                notificationService.sendPersonalNotification(participant.getLoginId(),
                                    new NotificationMessage(
                                        "MEETING_INVITE",
                                        "회의 참석 요청",
                                        user.getName() + "님이 회의에 초대했습니다: " + request.getTitle() + " (" + timeInfo + ")"
                                    ))
                        ));
            }
        } catch (Exception e) {
            // 알림 실패는 예약에 영향 없음
        }

        return ReservationResponse.from(reservation, getParticipants(reservation));
    }

    // 예약 취소 - 예약자 본인 또는 ADMIN만 가능
    @Transactional
    public void cancelReservation(Long reservationId, String loginId, boolean isAdmin) {
        RoomReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

        boolean isOwner = reservation.getUser().getLoginId().equals(loginId);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("본인이 예약한 회의실만 취소할 수 있습니다.");
        }

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
