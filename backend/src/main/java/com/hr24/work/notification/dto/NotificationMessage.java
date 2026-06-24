package com.hr24.work.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationMessage {

    private String type;    // SCHEDULE_DEPT, SCHEDULE_COMPANY, MEETING_RESERVATION
    private String title;   // 알림 제목
    private String message; // 알림 내용
}
