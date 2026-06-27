"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  sendPasswordResetCodeRequest,
  verifyPasswordResetCodeRequest,
  resetPasswordRequest,
} from "@/services/authService";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Field, FieldLabel } from "@/components/ui/field";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function PasswordFindPage() {
  const router = useRouter();

  const [step, setStep] = useState(1);

  const [loginId, setLoginId] = useState("");
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");

  const [resetToken, setResetToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [newPasswordConfirm, setNewPasswordConfirm] = useState("");

  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const clearMessage = () => {
    setMessage("");
    setErrorMessage("");
  };

  const handleSendCode = async (e) => {
    e.preventDefault();
    clearMessage();

    const trimmedLoginId = loginId.trim();
    const trimmedEmail = email.trim();

    if (!trimmedLoginId || !trimmedEmail) {
      setErrorMessage("아이디와 이메일을 입력해주세요.");
      return;
    }

    try {
      setLoading(true);

      await sendPasswordResetCodeRequest(trimmedLoginId, trimmedEmail);

      setMessage("인증코드를 발송했습니다.");
      setStep(2);
    } catch (error) {
      console.error("인증코드 발송 실패:", error);

      if (
        error.code === "INVALID_PASSWORD_RESET_INFO" ||
        error.status === 400
      ) {
        setErrorMessage("아이디 또는 이메일이 일치하지 않습니다.");
        return;
      }

      setErrorMessage("인증코드 발송 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyCode = async (e) => {
    e.preventDefault();
    clearMessage();

    if (!code.trim()) {
      setErrorMessage("인증코드를 입력해주세요.");
      return;
    }

    try {
      setLoading(true);

      const response = await verifyPasswordResetCodeRequest(
        loginId.trim(),
        email.trim(),
        code.trim(),
      );

      const data =
        response instanceof Response ? await response.json() : response;

      const issuedResetToken = data?.resetToken ?? data?.data?.resetToken;

      if (!issuedResetToken) {
        setErrorMessage(
          "비밀번호 재설정 토큰을 받지 못했습니다. 다시 인증해주세요.",
        );
        return;
      }

      setResetToken(issuedResetToken);
      setMessage("인증이 완료되었습니다. 새 비밀번호를 입력해주세요.");
      setStep(3);
    } catch (error) {
      console.error("인증코드 확인 실패:", error);
      setErrorMessage("인증코드가 올바르지 않거나 만료되었습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    clearMessage();

    if (!resetToken) {
      setErrorMessage("인증 정보가 없습니다. 인증코드를 다시 확인해주세요.");
      setStep(2);
      return;
    }

    if (!newPassword || !newPasswordConfirm) {
      setErrorMessage("새 비밀번호를 입력해주세요.");
      return;
    }

    if (newPassword !== newPasswordConfirm) {
      setErrorMessage("새 비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      setLoading(true);

      await resetPasswordRequest(resetToken, newPassword, newPasswordConfirm);

      alert("비밀번호가 변경되었습니다. 새 비밀번호로 로그인해주세요.");
      router.replace("/");
    } catch (error) {
      console.error("비밀번호 재설정 실패:", error);

      if (error.code === "SAME_AS_OLD_PASSWORD") {
        setErrorMessage("기존 비밀번호와 다른 비밀번호를 입력해주세요.");
        return;
      }

      if (error.code === "INVALID_PASSWORD_RESET_TOKEN") {
        setErrorMessage(
          "비밀번호 재설정 시간이 만료되었습니다. 처음부터 다시 진행해주세요.",
        );
        setStep(1);
        setResetToken("");
        setCode("");
        return;
      }

      setErrorMessage(error.message || "비밀번호 변경 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#eefdff] via-[#2a436b] to-[#061429] flex items-center justify-center px-4">
      <Card className="w-full max-w-md bg-[#1a2f4e] border-none rounded-2xl shadow-2xl">
        <CardHeader>
          <CardTitle className="text-3xl font-bold text-cyan-100">
            비밀번호 찾기
          </CardTitle>
          <CardDescription className="text-cyan-100/60">
            아이디와 이메일 인증 후 새 비밀번호를 설정합니다.
          </CardDescription>
        </CardHeader>

        <CardContent>
          <div className="mb-8">
            <div className="flex items-center justify-center">
              {[1, 2, 3].map((item) => (
                <div key={item} className="flex items-center">
                  <div
                    className={`flex h-8 w-8 items-center justify-center rounded-full text-sm font-bold transition-colors
            ${
              step > item
                ? "bg-cyan-200 text-[#1a2f4e]"
                : step === item
                  ? "bg-cyan-100 text-[#1a2f4e]"
                  : "bg-cyan-100/20 text-cyan-100/50"
            }`}
                  >
                    {step > item ? "✓" : item}
                  </div>

                  {item < 3 && (
                    <div
                      className={`mx-3 h-px w-12 transition-colors
              ${step > item ? "bg-cyan-200" : "bg-cyan-100/20"}`}
                    />
                  )}
                </div>
              ))}
            </div>
          </div>

          {step === 1 && (
            <form onSubmit={handleSendCode} className="flex flex-col gap-5">
              <Field>
                <FieldLabel htmlFor="loginId" className="text-cyan-100/80">
                  아이디
                </FieldLabel>
                <Input
                  id="loginId"
                  type="text"
                  value={loginId}
                  className="h-12 bg-cyan-100/70 text-[#1a2f4e]"
                  onChange={(e) => setLoginId(e.target.value)}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="email" className="text-cyan-100/80">
                  이메일
                </FieldLabel>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  className="h-12 bg-cyan-100/70 text-[#1a2f4e]"
                  onChange={(e) => setEmail(e.target.value)}
                />
              </Field>

              <Button
                type="submit"
                disabled={loading}
                className="w-full h-12 font-bold bg-cyan-200/75 text-[#1a2f4e] hover:bg-[#37b6c781]"
              >
                {loading ? "발송 중..." : "인증코드 발송"}
              </Button>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={handleVerifyCode} className="flex flex-col gap-5">
              <Field>
                <FieldLabel htmlFor="code" className="text-cyan-100/80">
                  인증코드
                </FieldLabel>
                <Input
                  id="code"
                  type="text"
                  value={code}
                  maxLength={6}
                  className="h-12 bg-cyan-100/70 text-[#1a2f4e]"
                  onChange={(e) => setCode(e.target.value)}
                />
              </Field>

              <Button
                type="submit"
                disabled={loading}
                className="w-full h-12 font-bold bg-cyan-200/75 text-[#1a2f4e] hover:bg-[#37b6c781]"
              >
                {loading ? "확인 중..." : "인증코드 확인"}
              </Button>

              <Button
                type="button"
                variant="ghost"
                className="text-cyan-100/70 hover:text-cyan-100"
                onClick={() => setStep(1)}
              >
                이전으로
              </Button>
            </form>
          )}

          {step === 3 && (
            <form
              onSubmit={handleResetPassword}
              className="flex flex-col gap-5"
            >
              <Field>
                <FieldLabel htmlFor="newPassword" className="text-cyan-100/80">
                  새 비밀번호
                </FieldLabel>
                <Input
                  id="newPassword"
                  type="password"
                  value={newPassword}
                  className="h-12 bg-cyan-100/70 text-[#1a2f4e]"
                  onChange={(e) => setNewPassword(e.target.value)}
                />
              </Field>

              <Field>
                <FieldLabel
                  htmlFor="newPasswordConfirm"
                  className="text-cyan-100/80"
                >
                  새 비밀번호 확인
                </FieldLabel>
                <Input
                  id="newPasswordConfirm"
                  type="password"
                  value={newPasswordConfirm}
                  className="h-12 bg-cyan-100/70 text-[#1a2f4e]"
                  onChange={(e) => setNewPasswordConfirm(e.target.value)}
                />
              </Field>

              <Button
                type="submit"
                disabled={loading}
                className="w-full h-12 font-bold bg-cyan-200/75 text-[#1a2f4e] hover:bg-[#37b6c781]"
              >
                {loading ? "변경 중..." : "비밀번호 변경"}
              </Button>
            </form>
          )}

          {message && (
            <div className="mt-5 rounded-md bg-cyan-100/10 px-4 py-3 text-sm text-cyan-100">
              {message}
            </div>
          )}

          {errorMessage && (
            <div className="mt-5 rounded-md bg-red-500/10 px-4 py-3 text-sm text-red-200">
              {errorMessage}
            </div>
          )}

          <Button
            type="button"
            variant="ghost"
            className="w-full mt-6 text-cyan-100/70 hover:text-cyan-100"
            onClick={() => router.replace("/")}
          >
            로그인 화면으로 돌아가기
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
