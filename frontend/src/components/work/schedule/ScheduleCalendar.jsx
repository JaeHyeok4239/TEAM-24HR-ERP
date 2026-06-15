"use client";

import { useEffect, useRef, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { apiRequest } from "@/lib/api";

const INIT_FORM = {
  title: "",
  date: "",
  startTime: "",
  endTime: "",
  scheduleType: "PERSONAL",
  location: "",
  memo: "",
};

export default function ScheduleCalendar() {
  const calendarRef = useRef(null);
  const [currentView, setCurrentView] = useState("dayGridMonth");
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState(INIT_FORM);
  const [events, setEvents] = useState([]);
  const [userId, setUserId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  // JWT 토큰에서 employeeId 직접 추출 (API 호출 불필요)
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) return;
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      if (payload?.employeeId) setUserId(payload.employeeId);
    } catch {
      console.error("로그인 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
    }
  }, []);

  // 일정 목록 불러오기 (현재 월 기준)
  const fetchSchedules = async (startDt, endDt) => {
    try {
      const res = await apiRequest(
        `/api/schedule?startDt=${startDt}&endDt=${endDt}`
      );
      const data = await res.json();
      // FullCalendar 이벤트 형태로 변환
      setEvents(
        data.map((s) => ({
          id: String(s.scheduleId),
          title: s.title,
          start: s.startDt,
          end: s.endDt,
          color: s.scheduleType === "COMPANY" ? "#ef4444"
               : s.scheduleType === "DEPT"    ? "#8b5cf6"
               : s.scheduleType === "PROJECT" ? "#f59e0b"
               : "#3b82f6",
        }))
      );
    } catch {
      console.error("일정 조회 실패");
    }
  };

  // 캘린더 날짜 범위 변경 시 일정 재조회
  const handleDatesSet = (info) => {
    const start = info.startStr.slice(0, 10);
    const end = info.endStr.slice(0, 10);
    fetchSchedules(start, end);
  };

  // 날짜 클릭 → 주간 뷰 전환 + 모달 날짜 세팅
  const handleDateClick = (info) => {
    setForm((prev) => ({ ...prev, date: info.dateStr }));
    const calApi = calendarRef.current.getApi();
    calApi.changeView("timeGridWeek", info.dateStr);
    setCurrentView("timeGridWeek");
  };

  const handleBackToMonth = () => {
    const calApi = calendarRef.current.getApi();
    calApi.changeView("dayGridMonth");
    setCurrentView("dayGridMonth");
  };

  const handleOpenAdd = () => {
    setForm(INIT_FORM);
    setError("");
    setShowAddModal(true);
  };

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  // 저장
  const handleSave = async () => {
    if (!form.title.trim()) { setError("제목을 입력해주세요."); return; }
    if (!form.date)          { setError("날짜를 선택해주세요."); return; }
    if (!userId)             { setError("사용자 정보를 불러오는 중입니다."); return; }

    setSaving(true);
    setError("");
    try {
      await apiRequest(`/api/schedule?userId=${userId}`, {
        method: "POST",
        body: JSON.stringify({
          title:        form.title,
          scheduleType: form.scheduleType,
          startDt:      form.date,
          endDt:        form.date,
          location:     form.location,
          memo:         form.memo,
          deptId:       form.scheduleType === "DEPT" ? form.deptId : null,
        }),
      });

      setShowAddModal(false);
      // 저장 후 캘린더 새로고침
      const calApi = calendarRef.current.getApi();
      const start = calApi.view.activeStart.toISOString().slice(0, 10);
      const end   = calApi.view.activeEnd.toISOString().slice(0, 10);
      fetchSchedules(start, end);
    } catch {
      setError("저장에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="p-6 h-full flex flex-col gap-4">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm text-gray-500">
          업무 관리 - 일정 관리
          {currentView === "timeGridWeek" && (
            <button
              onClick={handleBackToMonth}
              className="ml-2 text-blue-600 hover:underline"
            >
              ← 월별로 돌아가기
            </button>
          )}
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
        >
          + 일정 추가
        </button>
      </div>

      {/* 캘린더 */}
      <div className="bg-white rounded-lg shadow p-4 flex-1">
        <FullCalendar
          ref={calendarRef}
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          locale="ko"
          headerToolbar={{
            left: "prev,next today",
            center: "title",
            right: "",
          }}
          buttonText={{ today: "오늘" }}
          events={events}
          dateClick={handleDateClick}
          datesSet={handleDatesSet}
          slotMinTime="07:00:00"
          slotMaxTime="20:00:00"
          allDaySlot={false}
          height="100%"
          dayMaxEvents={3}
        />
      </div>

      {/* 일정 추가 모달 */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96 shadow-xl">
            <h2 className="text-lg font-semibold mb-4">일정 추가</h2>
            <div className="flex flex-col gap-3">
              <div>
                <label className="text-sm text-gray-600">제목</label>
                <input
                  name="title"
                  value={form.title}
                  onChange={handleChange}
                  type="text"
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  placeholder="일정 제목 입력"
                />
              </div>
              <div>
                <label className="text-sm text-gray-600">날짜</label>
                <input
                  name="date"
                  value={form.date}
                  onChange={handleChange}
                  type="date"
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                />
              </div>
              <div className="flex gap-2">
                <div className="flex-1">
                  <label className="text-sm text-gray-600">시작 시간</label>
                  <input
                    name="startTime"
                    value={form.startTime}
                    onChange={handleChange}
                    type="time"
                    className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  />
                </div>
                <div className="flex-1">
                  <label className="text-sm text-gray-600">종료 시간</label>
                  <input
                    name="endTime"
                    value={form.endTime}
                    onChange={handleChange}
                    type="time"
                    className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  />
                </div>
              </div>
              <div>
                <label className="text-sm text-gray-600">일정 분류</label>
                <select
                  name="scheduleType"
                  value={form.scheduleType}
                  onChange={handleChange}
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                >
                  <option value="PERSONAL">개인 일정</option>
                  <option value="DEPT">부서 일정</option>
                  <option value="COMPANY">회사 일정</option>
                  <option value="PROJECT">프로젝트</option>
                </select>
              </div>
              <div>
                <label className="text-sm text-gray-600">장소</label>
                <input
                  name="location"
                  value={form.location}
                  onChange={handleChange}
                  type="text"
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  placeholder="장소 입력"
                />
              </div>

              {error && (
                <p className="text-sm text-red-500">{error}</p>
              )}
            </div>
            <div className="flex justify-end gap-2 mt-6">
              <button
                onClick={() => setShowAddModal(false)}
                className="px-4 py-2 text-sm border rounded hover:bg-gray-50"
              >
                취소
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
              >
                {saving ? "저장 중..." : "저장"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
