package com.hr24.work.notification.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.work.notification.dto.NotificationMessage;
import com.hr24.work.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림", description = "저장된 알림 조회 및 읽음 처리 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회", description = "개인 알림(미읽음) + 부서/전사 알림(since 이후). since 미전달 시 7일 이내.")
    @GetMapping
    public ResponseEntity<List<NotificationMessage>> getMyNotifications(
            Authentication authentication,
            @RequestParam(required = false) String since) {
        String loginId = authentication.getName();
        LocalDateTime sinceTime = (since != null) ? LocalDateTime.parse(since) : null;
        return ResponseEntity.ok(notificationService.getNotificationsForUser(loginId, sinceTime));
    }

    @Operation(summary = "개인 알림 읽음 처리", description = "내 개인 알림을 모두 읽음 처리합니다.")
    @PatchMapping("/read")
    public ResponseEntity<Void> markAsRead(Authentication authentication) {
        String loginId = authentication.getName();
        notificationService.markPersonalNotificationsAsRead(loginId);
        return ResponseEntity.noContent().build();
    }
}
