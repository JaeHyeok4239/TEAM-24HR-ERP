"use client";

import { useState } from "react";
import { getMyInfoRequest, loginRequest } from "@/services/authService";
import { useAuthStore } from "@/store/authStore";

export default function LoginForm() {
  const authLogin = useAuthStore((state) => state.login);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const authLogout = useAuthStore((state) => state.logout);

  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      // 1. 아이디와 비밀번호로 로그인 요청
      const tokenData = await loginRequest(loginId, password);

      // 2. Access Token과 Refresh Token 저장
      authLogin(tokenData.accessToken, tokenData.refreshToken);

      // 3. Access Token을 이용해 현재 사용자 정보 조회
      const userInfo = await getMyInfoRequest(); 
      
      // 4. 사용자 정보를 Zustand 전역 상태에 저장
      setUserInfo(userInfo);

      // 5. 기존 오류 메시지 초기화
      setMessage("");
    } catch (error) {
      // 로그인 처리 중 일부만 성공한 경우에도 인증 정보 초기화
      authLogout(); 
      
      console.error("로그인 처리 실패:", error);
      setMessage("아이디 또는 비밀번호를 확인해주세요.");
    }
  };

  return (
    <div>
      <h1>24HR 로그인</h1>

      <form onSubmit={handleLogin}>
        <div>
          <label htmlFor="loginId">아이디</label>
          
          <input
            id="loginId"
            type="text"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
          />
        </div>

        <div>
          <label htmlFor="password">비밀번호</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <button type="submit">로그인</button>
      </form>

      {message && <p>{message}</p>}
    </div>
  );
}