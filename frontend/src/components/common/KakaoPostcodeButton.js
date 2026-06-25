"use client"

import { useState } from "react";

import { Button } from "@/components/ui/button";

const POSTCODE_SCRIPT_URL =
  "https://t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";

let postcodeScriptPromise = null;

const loadPostcodeScript = () => {
  if (typeof window === "undefined") {
    return Promise.reject(new Error("브라우저 환경에서만 사용할 수 있습니다."));
  }

  if (window.kakao?.Postcode || window.daum?.Postcode) {
    return Promise.resolve();
  }

  if (postcodeScriptPromise) {
    return postcodeScriptPromise;
  }

  postcodeScriptPromise = new Promise((resolve, reject) => {
    const existingScript = document.querySelector(
      'script[src="' + POSTCODE_SCRIPT_URL + '"]',
    );

    if (existingScript) {
      existingScript.addEventListener("load", resolve);
      existingScript.addEventListener("error", () => {
        reject(new Error("우편번호 검색 스크립트를 불러오지 못했습니다."));
      });
      return;
    }

    const script = document.createElement("script");
    script.src = POSTCODE_SCRIPT_URL;
    script.async = true;
    script.onload = resolve;
    script.onerror = () => {
      reject(new Error("우편번호 검색 스크립트를 불러오지 못했습니다."));
    };

    document.body.appendChild(script);
  });

  return postcodeScriptPromise;
};

export default function KakaoPostcodeButton({
  onComplete,
  disabled = false,
  className = "",
  children,
  title = "우편번호 검색",
}) {
  const [isLoading, setIsLoading] = useState(false);

  const handleSearchAddress = async () => {
    try {
      setIsLoading(true);

      await loadPostcodeScript();

      const Postcode = window.kakao?.Postcode || window.daum?.Postcode;

      if (!Postcode) {
        throw new Error("우편번호 검색 객체를 찾을 수 없습니다.");
      }

      new Postcode({
        theme: {
          bgColor: "#ffffff",
          searchBgColor: "#1a2f4e",
          contentBgColor: "#ffffff",
          pageBgColor: "#f1f5f9",
          textColor: "#1e293b",
          queryTextColor: "#ffffff",
          postcodeTextColor: "#2563eb",
          emphTextColor: "#2563eb",
          outlineColor: "#1a2f4e",
        },
        oncomplete: (data) => {
          const roadAddress = data.roadAddress;
          const jibunAddress = data.jibunAddress;
          const selectedAddress = roadAddress || jibunAddress;

          onComplete({
            zipcode: data.zonecode,
            address: selectedAddress,
          });
        },
      }).open();
    } catch (error) {
      console.error("주소 검색 실행 실패:", error);
      alert("주소 검색을 불러오지 못했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Button
      type="button"
      onClick={handleSearchAddress}
      disabled={disabled || isLoading}
      aria-label={title}
      title={title}
      className={
        className ||
        "h-9 shrink-0 bg-[#1a2f4e] px-3 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500"
      }
    >
      {isLoading ? "준비 중" : children || "검색"}
    </Button>
  );
}
