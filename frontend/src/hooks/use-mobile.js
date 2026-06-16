import * as React from "react"

const MOBILE_BREAKPOINT = 768

export function useIsMobile() {
  // 초기값을 lazy initializer로 즉시 계산
  // - 이 함수는 컴포넌트가 처음 마운트될 때 단 한 번만 실행됨
  // - useEffect 안에서 setState를 호출하지 않아도 되므로
  //   "Calling setState synchronously within an effect" 경고를 피할 수 있음
  const [isMobile, setIsMobile] = React.useState(() => {
    // Next.js는 서버에서도 렌더링되는데, 서버 환경에는 window 객체가 없음
    // 따라서 SSR 단계에서는 일단 false로 처리하고,
    // 실제 화면 너비는 클라이언트에서 useEffect가 실행될 때 다시 계산됨
    if (typeof window === "undefined") return false

    // 클라이언트(브라우저)에서 마운트될 때는
    // 현재 창 너비를 기준으로 모바일 여부를 바로 계산해서 초기값으로 사용
    return window.innerWidth < MOBILE_BREAKPOINT
  })

  React.useEffect(() => {
    // matchMedia: 미디어 쿼리 변경을 감지할 수 있는 브라우저 API
    // 화면 너비가 MOBILE_BREAKPOINT - 1(=767px) 이하인지 여부를 감시함
    const mql = window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT - 1}px)`)

    // 화면 크기(브레이크포인트 경계)가 바뀔 때 호출되는 콜백
    // - "외부 시스템(브라우저 창 크기)의 변화"를 React 상태에 반영하는 역할
    // - 이런 "콜백 안에서의 setState"는 React가 권장하는 정상 패턴
    const onChange = () => {
      setIsMobile(window.innerWidth < MOBILE_BREAKPOINT)
    }

    // 미디어 쿼리 상태가 바뀔 때마다 onChange 실행되도록 구독 등록
    mql.addEventListener("change", onChange)

    // cleanup 함수: 컴포넌트가 언마운트되거나 effect가 재실행되기 전에
    // 등록했던 이벤트 리스너를 해제 (메모리 누수 방지)
    return () => mql.removeEventListener("change", onChange)
  }, []) // 빈 배열: 컴포넌트가 마운트될 때 한 번만 등록/구독

  // boolean 값을 그대로 반환
  // (초기값이 이미 useState 시점에서 boolean으로 계산되었으므로 !! 변환 불필요)
  return isMobile
}