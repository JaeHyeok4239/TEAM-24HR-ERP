package com.hr24.work.schedule.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.work.schedule.dto.HolidayResponse;
import com.hr24.work.schedule.dto.ScheduleRequest;
import com.hr24.work.schedule.dto.ScheduleResponse;
import com.hr24.work.schedule.service.HolidayFetchService;
import com.hr24.work.schedule.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "일정 관리", description = "일정 및 공휴일 관련 API")
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final HolidayFetchService holidayFetchService;

    // 기간 내 일정 목록 조회 - 달력 화면에서 startDt~endDt 범위로 호출
    @Operation(summary = "기간별 일정 조회", description = "시작일~종료일 범위에 걸치는 일정 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(
            @RequestParam("startDt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDt,
            @RequestParam("endDt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDt) {
        return ResponseEntity.ok(scheduleService.getSchedules(startDt, endDt));
    }

    // 연도별 공휴일 목록 조회
    @Operation(summary = "공휴일 조회", description = "해당 연도의 공휴일 목록을 반환합니다.")
    @GetMapping("/holidays")
    public ResponseEntity<List<HolidayResponse>> getHolidays(@RequestParam("year") Integer year) {
        return ResponseEntity.ok(scheduleService.getHolidays(year));
    }

    // 공공 API에서 공휴일 데이터 가져와 DB 저장 (관리자용)
    @Operation(summary = "공휴일 갱신", description = "공공데이터 API에서 해당 연도의 공휴일을 가져와 DB에 저장합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/holidays/fetch")
    public ResponseEntity<String> fetchHolidays(@RequestParam("year") Integer year) {
        int count = holidayFetchService.fetchAndSave(year);
        return ResponseEntity.ok(year + "년 공휴일 " + count + "건 저장 완료");
    }

    // 일정 등록 - scheduleType(PERSONAL/DEPT/COMPANY)에 따라 부서 연결 여부 결정
    @Operation(summary = "일정 등록", description = "새 일정을 등록합니다. DEPT 타입이면 deptId 필수.")
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @RequestBody ScheduleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(scheduleService.createSchedule(authentication.getName(), request));
    }

    // 일정 수정 - 작성자 본인 또는 ADMIN만 가능
    @Operation(summary = "일정 수정", description = "작성자 본인 또는 ADMIN만 수정할 수 있습니다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request,
            Authentication authentication) {
        String loginId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, request, loginId, isAdmin));
    }

    // 일정 삭제 - 작성자 본인 또는 ADMIN만 가능
    @Operation(summary = "일정 삭제", description = "작성자 본인 또는 ADMIN만 삭제할 수 있습니다.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long scheduleId,
            Authentication authentication) {
        String loginId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        scheduleService.deleteSchedule(scheduleId, loginId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
