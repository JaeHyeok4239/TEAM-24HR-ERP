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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.work.schedule.dto.HolidayResponse;
import com.hr24.work.schedule.dto.ScheduleRequest;
import com.hr24.work.schedule.dto.ScheduleResponse;
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

    // 기간 내 일정 목록 조회 - 달력 화면에서 startDt~endDt 범위로 호출
    @Operation(summary = "기간별 일정 조회", description = "시작일~종료일 범위에 걸치는 일정 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDt) {
        return ResponseEntity.ok(scheduleService.getSchedules(startDt, endDt));
    }

    // 연도별 공휴일 목록 조회
    @Operation(summary = "공휴일 조회", description = "해당 연도의 공휴일 목록을 반환합니다.")
    @GetMapping("/holidays")
    public ResponseEntity<List<HolidayResponse>> getHolidays(@RequestParam Integer year) {
        return ResponseEntity.ok(scheduleService.getHolidays(year));
    }

    // 일정 등록 - scheduleType(PERSONAL/DEPT/COMPANY)에 따라 부서 연결 여부 결정
    @Operation(summary = "일정 등록", description = "새 일정을 등록합니다. DEPT 타입이면 deptId 필수.")
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @RequestParam Long userId,
            @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(userId, request));
    }

    // 일정 수정 - 제목, 날짜, 장소, 메모 변경
    @Operation(summary = "일정 수정", description = "일정 내용을 수정합니다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, request));
    }

    // 일정 삭제
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}
