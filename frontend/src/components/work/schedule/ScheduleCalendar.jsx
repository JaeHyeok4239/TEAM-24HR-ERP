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
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [deleting, setDeleting] = useState(false);

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
          start: s.startTime ? `${s.startDt}T${s.startTime}` : s.startDt,
          end:   s.endTime   ? `${s.endDt}T${s.endTime}`     : s.endDt,
          allDay: !s.startTime,
          color: s.scheduleType === "COMPANY" ? "#ef4444"
               : s.scheduleType === "DEPT"    ? "#8b5cf6"
               : s.scheduleType === "PROJECT" ? "#f59e0b"
               : "#3b82f6",
          extendedProps: {
            startTime: s.startTime,
            endTime: s.endTime,
            location: s.location,
            memo: s.memo,
          },
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

  // 일정 클릭 → 상세 모달
  const handleEventClick = (info) => {
    setSelectedEvent({
      id: info.event.id,
      title: info.event.title,
      start: info.event.startStr?.slice(0, 10),
      end: info.event.endStr?.slice(0, 10),
      color: info.event.backgroundColor,
      startTime: info.event.extendedProps.startTime,
      endTime: info.event.extendedProps.endTime,
      location: info.event.extendedProps.location,
      memo: info.event.extendedProps.memo,
    });
  };

  // 삭제
  const handleDelete = async () => {
    if (!selectedEvent) return;
    setDeleting(true);
    try {
      await apiRequest(`/api/schedule/${selectedEvent.id}`, { method: "DELETE" });
      setSelectedEvent(null);
      const calApi = calendarRef.current.getApi();
      const start = calApi.view.activeStart.toISOString().slice(0, 10);
      const end   = calApi.view.activeEnd.toISOString().slice(0, 10);
      fetchSchedules(start, end);
    } catch {
      alert("삭제에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setDeleting(false);
    }
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
          startTime:    form.startTime || null,
          endTime:      form.endTime   || null,
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
          eventClick={handleEventClick}
          slotMinTime="07:00:00"
          slotMaxTime="20:00:00"
          allDaySlot={true}
          allDayText="종일"
          height="100%"
          dayMaxEvents={3}
        />
      </div>

      {/* 일정 상세/삭제 모달 */}
      {selectedEvent && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-80 shadow-xl">
            <h2 className="text-lg font-semibold mb-4">일정 상세</h2>
            <div className="flex flex-col gap-2 text-sm text-gray-700">
              <div className="flex gap-2">
                <span
                  className="w-3 h-3 rounded-full mt-1 flex-shrink-0"
                  style={{ backgroundColor: selectedEvent.color }}
                />
                <span className="font-medium text-base">{selectedEvent.title}</span>
              </div>
              <p className="text-gray-500 ml-5">
                {selectedEvent.start}
                {selectedEvent.startTime && (
                  <span className="ml-2">
                    {selectedEvent.startTime}
                    {selectedEvent.endTime && ` ~ ${selectedEvent.endTime}`}
                  </span>
                )}
              </p>
              {selectedEvent.location && (
                <div className="ml-5 flex gap-1">
                  <span className="text-gray-400">장소</span>
                  <span>{selectedEvent.location}</span>
                </div>
              )}
              {selectedEvent.memo && (
                <div className="ml-5 flex gap-1">
                  <span className="text-gray-400">내용</span>
                  <span className="whitespace-pre-wrap">{selectedEvent.memo}</span>
                </div>
              )}
            </div>
            <div className="flex justify-between mt-6">
              <button
                onClick={handleDelete}
                disabled={deleting}
                className="px-4 py-2 text-sm bg-red-500 text-white rounded hover:bg-red-600 disabled:opacity-50"
              >
                {deleting ? "삭제 중..." : "삭제"}
              </button>
              <button
                onClick={() => setSelectedEvent(null)}
                className="px-4 py-2 text-sm border rounded hover:bg-gray-50"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}

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
              <div>
                <label className="text-sm text-gray-600">내용</label>
                <textarea
                  name="memo"
                  value={form.memo}
                  onChange={handleChange}
                  rows={3}
                  className="w-full border rounded px-3 py-2 mt-1 text-sm resize-none"
                  placeholder="내용 입력"
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
