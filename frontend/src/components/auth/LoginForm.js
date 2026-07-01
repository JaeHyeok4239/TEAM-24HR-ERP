"use client";

import { useState } from "react";
import { loginRequest } from "@/services/authService";
import { getMyInfoRequest } from "@/services/userService";
import { useAuthStore } from "@/store/authStore";
import { useRouter } from "next/navigation";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Field, FieldLabel } from "@/components/ui/field";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";

export default function LoginForm() {
  const authLogin = useAuthStore((state) => state.login);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const authLogout = useAuthStore((state) => state.logout);

  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const router = useRouter();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const tokenData = await loginRequest(loginId, password);

      authLogin(tokenData.accessToken);

      const userInfo = await getMyInfoRequest();

      setUserInfo(userInfo);
      setMessage("");

      if (userInfo.isFirstLogin === "Y") {
        router.replace("/user/password-change");
      }
    } catch (error) {
      authLogout();
      setMessage("아이디 또는 비밀번호를 확인해주세요.");
    }
  };
  return (
    <div className="min-h-dvh bg-gradient-to-br from-[#eefdff] via-[#2a436b] to-[#061429] flex items-center justify-center px-4 py-6">
      <Card className="flex flex-col md:flex-row w-full max-w-4xl h-auto bg-[#1a2f4e] overflow-hidden p-0 rounded-2xl shadow-2xl">
        {/* 왼쪽 영역: 로그인 폼 컨테이너 */}
        <div className="bg-[#1a2f4e] justify-center flex flex-col flex-1 p-6 sm:p-8 lg:p-10 [@media(max-height:720px)]:p-6">
          <CardHeader className="flex flex-col p-0 mb-4 items-end [@media(max-height:720px)]:mb-3">
            <CardTitle className="p-2 text-4xl [@media(max-height:720px)]:text-3xl italic font-extrabold tracking-tight text-balance text-[#dbfdffd2]">
              24HR
            </CardTitle>
            <CardDescription className="text-[#718ba8] p-2">
              인사 관리 ERP 시스템
            </CardDescription>
          </CardHeader>

          <hr className="mb-8 [@media(max-height:720px)]:mb-5" />

          <form
            onSubmit={handleLogin}
            className="flex flex-col gap-5 [@media(max-height:720px)]:gap-4"
          >
            <CardContent className="flex flex-col gap-5 [@media(max-height:720px)]:gap-4 justify-center p-0">
              <Field>
                <FieldLabel htmlFor="loginId" className="text-cyan-100/80">
                  아이디
                </FieldLabel>
                <Input
                  id="loginId"
                  type="text"
                  value={loginId}
                  className="transition-opacity duration-300 hover:opacity-70 focus:opacity-70 w-full text-[#1a2f4e] h-12 [@media(max-height:720px)]:h-10 bg-cyan-100/70 rounded-lg mt-1"
                  onChange={(e) => setLoginId(e.target.value)}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="password" className="text-cyan-100/80">
                  비밀번호
                </FieldLabel>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  className="transition-opacity duration-300 hover:opacity-70 focus:opacity-70 w-full text-[#1a2f4e] h-12 [@media(max-height:720px)]:h-10 bg-cyan-100/70 rounded-lg mt-1"
                  onChange={(e) => setPassword(e.target.value)}
                />
              </Field>

              {message && (
                <div className="text-red-300 text-sm mt-1">{message}</div>
              )}
            </CardContent>

            <CardFooter className="flex flex-col justify-center p-0 mt-5 [@media(max-height:720px)]:mt-3">
              <Button
                type="submit"
                variant="primary"
                className="w-full font-bold bg-cyan-200/75 h-12 [@media(max-height:720px)]:h-10 text-[#1a2f4e] hover:bg-[#37b6c781] transition-colors duration-300"
              >
                로그인
              </Button>

              <Link
                href="/user/password-find"
                className="mt-4 [@media(max-height:720px)]:mt-3 text-sm text-cyan-100/70 hover:text-cyan-100 underline underline-offset-4 transition-colors"
              >
                비밀번호를 잊으셨나요?
              </Link>
            </CardFooter>
          </form>
        </div>

        {/* 오른쪽 이미지 영역 */}
        <div
          className="relative -ml-10 -mr-10 hidden md:block [@media(max-height:640px)]:hidden flex-1 bg-cover bg-center transition-opacity duration-300 hover:opacity-70"
          style={{
            backgroundImage: `url('/logo.png')`,
          }}
        >
          <div className="absolute inset-0 bg-gradient-to-r from-black/40 to-transparent pointer-events-none -ml-px" />
        </div>
      </Card>
    </div>
  );
}
