package com.hr24.work.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Operation(summary = "내 알림 목록 조회", description = "로그인 후 미확인 알림을 불러옵니다. 개인 알림(미읽음) + 부서/전사 알림(7일 이내).")
    @GetMapping
    public ResponseEntity<List<NotificationMessage>> getMyNotifications(Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(notificationService.getNotificationsForUser(loginId));
    }

    @Operation(summary = "개인 알림 읽음 처리", description = "내 개인 알림을 모두 읽음 처리합니다.")
    @PatchMapping("/read")
    public ResponseEntity<Void> markAsRead(Authentication authentication) {
        String loginId = authentication.getName();
        notificationService.markPersonalNotificationsAsRead(loginId);
        return ResponseEntity.noContent().build();
    }
}
