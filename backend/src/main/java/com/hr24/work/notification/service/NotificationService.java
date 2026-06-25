package com.hr24.work.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.repository.UserRepository;
import com.hr24.work.notification.dto.NotificationMessage;
import com.hr24.work.notification.entity.Notification;
import com.hr24.work.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 전사 공지 - DB 저장 + WebSocket 전송
    @Transactional
    public void sendCompanyNotification(NotificationMessage message) {
        notificationRepository.save(Notification.builder()
            .scope("COMPANY")
            .type(message.getType())
            .title(message.getTitle())
            .message(message.getMessage())
            .build());
        try {
            messagingTemplate.convertAndSend("/topic/notifications/company", message);
        } catch (Exception ignored) {}
    }

    // 부서 공지 - DB 저장 + WebSocket 전송
    @Transactional
    public void sendDeptNotification(String departmentName, NotificationMessage message) {
        notificationRepository.save(Notification.builder()
            .scope("DEPT")
            .departmentName(departmentName)
            .type(message.getType())
            .title(message.getTitle())
            .message(message.getMessage())
            .build());
        try {
            messagingTemplate.convertAndSend("/topic/notifications/dept/" + departmentName, message);
        } catch (Exception ignored) {}
    }

    // 개인 알림 - DB 저장 + WebSocket 전송
    @Transactional
    public void sendPersonalNotification(String loginId, NotificationMessage message) {
        notificationRepository.save(Notification.builder()
            .scope("PERSONAL")
            .loginId(loginId)
            .type(message.getType())
            .title(message.getTitle())
            .message(message.getMessage())
            .build());
        try {
            messagingTemplate.convertAndSendToUser(loginId, "/queue/notifications", message);
        } catch (Exception ignored) {}
    }

    // 사용자의 미확인 알림 조회 (로그인 후 불러오기용)
    public List<NotificationMessage> getNotificationsForUser(String loginId) {
        String departmentName = userRepository.findByLoginId(loginId)
            .map(u -> u.getDepartment() != null ? u.getDepartment().getDepartmentName() : null)
            .orElse(null);

        LocalDateTime since = LocalDateTime.now().minusDays(7);

        return notificationRepository.findNotificationsForUser(loginId, departmentName, since)
            .stream()
            .map(n -> new NotificationMessage(n.getType(), n.getTitle(), n.getMessage()))
            .collect(Collectors.toList());
    }

    // 개인 알림 읽음 처리
    @Transactional
    public void markPersonalNotificationsAsRead(String loginId) {
        notificationRepository.markPersonalAsRead(loginId);
    }
}
