"use client";

import { useState } from "react";
import { loginRequest } from "@/services/authService";
import { useAuthStore } from "@/store/authStore";

export default function LoginForm() {
  const authLogin = useAuthStore((state) => state.login);

  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const data = await loginRequest(loginId, password);

      authLogin(data.accessToken, data.refreshToken);

      setMessage("");
    } catch (error) {
      setMessage("로그인 실패");
    }
  };

  return (
    <div>
      <h1>24HR 로그인</h1>

      <form onSubmit={handleLogin}>
        <div>
          <label>아이디</label>
          <input
            type="text"
            value={loginId}
            onChange={(e) => setLoginId(e.target.value)}
          />
        </div>

        <div>
          <label>비밀번호</label>
          <input
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