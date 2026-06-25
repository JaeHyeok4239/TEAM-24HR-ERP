"use client";

import { useEffect, useState, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuthStore } from "@/store/authStore";

// 알림 고유 키 생성 (title + message 조합)
const notifKey = (n) => `${n.title}||${n.message}`;

export function useNotification() {
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);
  const { userInfo, accessToken } = useAuthStore();

  const storageKey = `notif_dismissed_${userInfo?.loginId || "unknown"}`;

  const getDismissed = () => {
    try {
      return new Set(JSON.parse(localStorage.getItem(storageKey) || "[]"));
    } catch {
      return new Set();
    }
  };

  const saveDismissed = (set) => {
    // 최대 300개 유지
    const arr = [...set].slice(-300);
    localStorage.setItem(storageKey, JSON.stringify(arr));
  };

  const filterDismissed = (list) => {
    const dismissed = getDismissed();
    return list.filter((n) => !dismissed.has(notifKey(n)));
  };

  // 로그인 시 저장된 미확인 알림 불러오기 (이미 닫은 것은 제외)
  useEffect(() => {
    if (!userInfo || !accessToken) return;

    fetch("http://localhost:8080/api/notifications", {
      headers: { Authorization: `Bearer ${accessToken}` },
    })
      .then((r) => (r.ok ? r.json() : []))
      .then((data) => setNotifications(filterDismissed(data)))
      .catch(() => {});
  }, [userInfo?.loginId]);

  // WebSocket 실시간 알림 구독
  useEffect(() => {
    if (!userInfo || !accessToken) return;

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      onConnect: () => {
        client.subscribe("/topic/notifications/company", (msg) => {
          setNotifications((prev) => [JSON.parse(msg.body), ...prev]);
        });

        if (userInfo.departmentName) {
          client.subscribe(`/topic/notifications/dept/${userInfo.departmentName}`, (msg) => {
            setNotifications((prev) => [JSON.parse(msg.body), ...prev]);
          });
        }

        client.subscribe("/user/queue/notifications", (msg) => {
          setNotifications((prev) => [JSON.parse(msg.body), ...prev]);
        });
      },
      onDisconnect: () => {},
      onStompError: () => {},
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [userInfo, accessToken]);

  const removeNotification = (index) => {
    const n = notifications[index];
    // localStorage에 dismissed 등록
    const dismissed = getDismissed();
    dismissed.add(notifKey(n));
    saveDismissed(dismissed);

    setNotifications((prev) => prev.filter((_, i) => i !== index));

    // 개인 알림 읽음 처리 (DB 정리용)
    if (accessToken) {
      fetch("http://localhost:8080/api/notifications/read", {
        method: "PATCH",
        headers: { Authorization: `Bearer ${accessToken}` },
      }).catch(() => {});
    }
  };

  const clearNotifications = () => {
    // 현재 알림 전체 dismissed 등록
    const dismissed = getDismissed();
    notifications.forEach((n) => dismissed.add(notifKey(n)));
    saveDismissed(dismissed);

    setNotifications([]);

    if (accessToken) {
      fetch("http://localhost:8080/api/notifications/read", {
        method: "PATCH",
        headers: { Authorization: `Bearer ${accessToken}` },
      }).catch(() => {});
    }
  };

  return { notifications, removeNotification, clearNotifications };
}
