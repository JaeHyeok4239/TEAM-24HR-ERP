
package com.hr24.work.meeting.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.work.meeting.dto.MeetingRoomResponse;
import com.hr24.work.meeting.dto.ReservationRequest;
import com.hr24.work.meeting.dto.ReservationResponse;
import com.hr24.work.meeting.service.MeetingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회의실 예약", description = "회의실 조회 및 예약 관련 API")
@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    // 사용 가능한 회의실 목록 조회
    @Operation(summary = "회의실 목록 조회", description = "ACTIVE 상태인 회의실 목록을 반환합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<MeetingRoomResponse>> getRooms() {
        return ResponseEntity.ok(meetingService.getActiveRooms());
    }

    // 특정 날짜의 전체 예약 현황 조회 (달력/타임라인 용도)
    @Operation(summary = "날짜별 예약 조회", description = "해당 날짜의 전체 예약 목록을 반환합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(meetingService.getReservationsByDate(date));
    }

    // 로그인한 사용자의 예약 목록 조회
    @Operation(summary = "내 예약 목록 조회", description = "JWT 토큰에서 추출한 loginId로 해당 사용자의 예약 목록을 반환합니다.")
    @GetMapping("/reservations/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(meetingService.getMyReservations(loginId));
    }

    // 새 예약 생성 - 시간 중복 체크 포함
    @Operation(summary = "예약 생성", description = "회의실 예약을 생성합니다. 시간 중복 시 400 반환.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            Authentication authentication,
            @RequestBody ReservationRequest request) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(meetingService.createReservation(loginId, request));
    }

    // 예약 취소 - status를 CANCELLED로 변경
    @Operation(summary = "예약 취소", description = "예약 ID로 해당 예약을 취소 처리합니다.")
    @PatchMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        meetingService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
