package com.hr24.work.notification.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.hr24.work.notification.dto.NotificationMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // 전사 공지 - /topic/notifications/company 구독자 전체에게 전송
    public void sendCompanyNotification(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications/company", message);
    }

    // 부서 공지 - /topic/notifications/dept/{부서명} 구독자에게 전송
    public void sendDeptNotification(String departmentName, NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications/dept/" + departmentName, message);
    }

    // 개인 알림 - /user/{loginId}/queue/notifications 로 전송
    public void sendPersonalNotification(String loginId, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(loginId, "/queue/notifications", message);
    }
}
