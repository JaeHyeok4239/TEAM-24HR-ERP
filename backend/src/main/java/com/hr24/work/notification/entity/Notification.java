package com.hr24.work.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_gen")
    @SequenceGenerator(name = "notification_gen", sequenceName = "work_notification_seq", allocationSize = 1)
    @Column(name = "notification_id")
    private Long notificationId;

    // PERSONAL 알림 수신자 loginId
    @Column(name = "login_id", length = 50)
    private String loginId;

    // DEPT 알림 대상 부서명
    @Column(name = "department_name", length = 100)
    private String departmentName;

    // PERSONAL / DEPT / COMPANY
    @Column(name = "scope", length = 20, nullable = false)
    private String scope;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "message", length = 1000, nullable = false)
    private String message;

    @Column(name = "is_read", length = 1, nullable = false)
    @Builder.Default
    private String isRead = "N";

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
