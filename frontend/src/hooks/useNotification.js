"use client";

import { useEffect, useState, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuthStore } from "@/store/authStore";

export function useNotification() {
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);
  const { userInfo, accessToken } = useAuthStore();

  // 로그인 시 저장된 미확인 알림 불러오기
  useEffect(() => {
    if (!userInfo || !accessToken) return;

    fetch("http://localhost:8080/api/notifications", {
      headers: { Authorization: `Bearer ${accessToken}` },
    })
      .then((r) => (r.ok ? r.json() : []))
      .then((data) => setNotifications(data))
      .catch(() => {});
  }, [userInfo?.loginId]);

  useEffect(() => {
    if (!userInfo || !accessToken) return;

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`, // JWT로 사용자 인증
      },
      onConnect: () => {
        // 전사 알림 구독 (모든 사용자)
        client.subscribe("/topic/notifications/company", (msg) => {
          setNotifications((prev) => [JSON.parse(msg.body), ...prev]);
        });

        // 부서 알림 구독 (내 부서 일정)
        if (userInfo.departmentName) {
          client.subscribe(`/topic/notifications/dept/${userInfo.departmentName}`, (msg) => {
            setNotifications((prev) => [JSON.parse(msg.body), ...prev]);
          });
        }

        // 개인 알림 구독 (회의실 예약 등)
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

  const clearNotifications = () => {
    setNotifications([]);
    if (accessToken) {
      fetch("http://localhost:8080/api/notifications/read", {
        method: "PATCH",
        headers: { Authorization: `Bearer ${accessToken}` },
      }).catch(() => {});
    }
  };

  return { notifications, clearNotifications };
}
