package com.hr24.work.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.work.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 사용자의 미확인 알림 조회
    // - PERSONAL: 본인 loginId + 미읽음
    // - DEPT: 소속 부서명 + 최근 7일
    // - COMPANY: 전체 + 최근 7일
    @Query("""
        SELECT n FROM Notification n
        WHERE (n.scope = 'PERSONAL' AND n.loginId = :loginId AND n.isRead = 'N')
        OR (n.scope = 'DEPT' AND n.departmentName = :departmentName AND n.createdAt >= :since)
        OR (n.scope = 'COMPANY' AND n.createdAt >= :since)
        ORDER BY n.createdAt DESC
        """)
    List<Notification> findNotificationsForUser(
        @Param("loginId") String loginId,
        @Param("departmentName") String departmentName,
        @Param("since") LocalDateTime since
    );

    // 개인 알림 일괄 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 'Y' WHERE n.scope = 'PERSONAL' AND n.loginId = :loginId AND n.isRead = 'N'")
    void markPersonalAsRead(@Param("loginId") String loginId);
}
