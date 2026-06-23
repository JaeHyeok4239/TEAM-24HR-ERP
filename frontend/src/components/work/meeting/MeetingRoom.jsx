"use client";

import { useEffect, useState, useCallback } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { apiRequest } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";

const HOURS = Array.from({ length: 12 }, (_, i) => i + 7); // 07~18 (18:00~19:00 슬롯까지)
const TIME_OPTIONS = [];
for (let h = 7; h <= 19; h++) {
  TIME_OPTIONS.push(`${String(h).padStart(2, "0")}:00`);
  if (h < 19) TIME_OPTIONS.push(`${String(h).padStart(2, "0")}:30`);
}
const DAYS = ["일", "월", "화", "수", "목", "금", "토"];
const MONTHS = ["1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"];

const INIT_FORM = { roomId: "", title: "", startTime: "", endTime: "", purpose: "" };

function toDateStr(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

export default function MeetingRoom() {
  const userInfo = useAuthStore((state) => state.userInfo);
  const today = new Date();
  const [calYear, setCalYear] = useState(today.getFullYear());
  const [calMonth, setCalMonth] = useState(today.getMonth());
  const [selectedDate, setSelectedDate] = useState(toDateStr(today));
  const [tab, setTab] = useState("all");
  const [rooms, setRooms] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [myReservations, setMyReservations] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(INIT_FORM);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [detailReservation, setDetailReservation] = useState(null);

  // 회의실 목록
  useEffect(() => {
    apiRequest("/api/meeting/rooms")
      .then((r) => r.json())
      .then(setRooms)
      .catch(() => {});
  }, []);

  // 날짜별 예약 현황
  const fetchReservations = useCallback(async (date) => {
    try {
      const res = await apiRequest(`/api/meeting/reservations?date=${date}`);
      setReservations(await res.json());
    } catch {}
  }, []);

  // 내 예약 목록
  const fetchMyReservations = useCallback(async () => {
    if (!userInfo) return;
    try {
      const res = await apiRequest("/api/meeting/reservations/my");
      setMyReservations(await res.json());
    } catch {}
  }, [userInfo]);

  useEffect(() => { fetchReservations(selectedDate); }, [selectedDate, fetchReservations]);
  useEffect(() => { fetchMyReservations(); }, [fetchMyReservations]);

  // 슬롯 점유 정보
  const getSlotInfo = (roomId, hour) => {
    const slotStart = `${String(hour).padStart(2, "0")}:00`;
    const slotEnd = `${String(hour + 1).padStart(2, "0")}:00`;
    return reservations.find(
      (r) => r.roomId === roomId && r.status === "CONFIRMED" && r.startTime < slotEnd && r.endTime > slotStart
    ) || null;
  };

  // 해당 날짜에 모든 슬롯이 예약된 회의실 여부
  const isRoomFullyBooked = (roomId) =>
    HOURS.every((hour) => getSlotInfo(roomId, hour) !== null);

  // 타임슬롯 클릭 → 모달 오픈
  const handleSlotClick = (roomId, hour) => {
    setForm({
      ...INIT_FORM,
      roomId: String(roomId),
      title: userInfo?.departmentName || "",
      startTime: `${String(hour).padStart(2, "0")}:00`,
      endTime: `${String(hour + 1).padStart(2, "0")}:00`,
    });
    setError("");
    setShowModal(true);
  };

  // 예약 저장
  const handleSave = async () => {
    if (!form.title.trim()) { setError("제목을 입력해주세요."); return; }
    if (!form.roomId) { setError("회의실을 선택해주세요."); return; }
    if (!form.startTime || !form.endTime) { setError("시간을 선택해주세요."); return; }
    if (form.startTime >= form.endTime) { setError("종료 시간은 시작 시간보다 늦어야 해요."); return; }

    setSaving(true);
    setError("");
    try {
      await apiRequest("/api/meeting/reservations", {
        method: "POST",
        body: JSON.stringify({
          roomId: Number(form.roomId),
          title: form.title,
          rsvDate: selectedDate,
          startTime: form.startTime,
          endTime: form.endTime,
          purpose: form.purpose,
          participantIds: [],
        }),
      });
      setShowModal(false);
      setForm(INIT_FORM);
      fetchReservations(selectedDate);
      fetchMyReservations();
    } catch {
      setError("예약에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  // 예약 취소
  const handleCancel = async (reservationId) => {
    if (!confirm("예약을 취소할까요?")) return;
    try {
      await apiRequest(`/api/meeting/reservations/${reservationId}/cancel`, { method: "PATCH" });
      fetchReservations(selectedDate);
      fetchMyReservations();
    } catch {
      alert("취소에 실패했습니다.");
    }
  };

  // 미니 캘린더 셀 생성 
  const buildCalendar = () => {
    const firstDay = new Date(calYear, calMonth, 1).getDay();
    const daysInMonth = new Date(calYear, calMonth + 1, 0).getDate();
    const cells = Array(firstDay).fill(null);
    for (let d = 1; d <= daysInMonth; d++) cells.push(d);
    return cells;
  };

  const calCells = buildCalendar();
  const todayStr = toDateStr(today);

  const prevMonth = () => {
    if (calMonth === 0) { setCalMonth(11); setCalYear((y) => y - 1); }
    else setCalMonth((m) => m - 1);
  };
  const nextMonth = () => {
    if (calMonth === 11) { setCalMonth(0); setCalYear((y) => y + 1); }
    else setCalMonth((m) => m + 1);
  };

  return (
    <div className="p-6 h-full flex flex-col gap-4">
      {/* 헤더 */}

      {/* 탭 */}
      <div className="flex gap-0 border-b border-gray-200">
        {[["all", "전체 현황"], ["my", "내 예약"]].map(([key, label]) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`px-5 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === key
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-gray-400 hover:text-gray-600"
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {/* 메인 영역 */}
      <div className="flex flex-1 gap-4 min-h-0">
        {/* 왼쪽: 미니 캘린더 + 회의실 목록 */}
        <div className="w-52 flex-shrink-0 flex flex-col gap-3">
          {/* 미니 캘린더 */}
          <div className="bg-white rounded-lg shadow p-3">
            <div className="flex items-center justify-between mb-2">
              <button onClick={prevMonth} className="p-1 hover:bg-gray-100 rounded">
                <ChevronLeft size={14} />
              </button>
              <span className="text-xs font-semibold text-gray-700">
                {calYear}년 {MONTHS[calMonth]}
              </span>
              <button onClick={nextMonth} className="p-1 hover:bg-gray-100 rounded">
                <ChevronRight size={14} />
              </button>
            </div>
            <div className="grid grid-cols-7 text-center">
              {DAYS.map((d, i) => (
                <div key={d} className={`text-[10px] font-medium py-1 ${i === 0 ? "text-red-400" : i === 6 ? "text-blue-400" : "text-gray-400"}`}>
                  {d}
                </div>
              ))}
              {calCells.map((day, idx) => {
                if (!day) return <div key={`e-${idx}`} />;
                const dateStr = `${calYear}-${String(calMonth + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
                const isToday = dateStr === todayStr;
                const isSel = dateStr === selectedDate;
                const col = idx % 7;
                return (
                  <button
                    key={dateStr}
                    onClick={() => setSelectedDate(dateStr)}
                    className={`text-[11px] mx-auto w-6 h-6 flex items-center justify-center rounded-full transition-colors ${
                      isSel ? "bg-blue-600 text-white font-bold"
                      : isToday ? "bg-blue-100 text-blue-700 font-semibold"
                      : col === 0 ? "text-red-400 hover:bg-gray-100"
                      : col === 6 ? "text-blue-400 hover:bg-gray-100"
                      : "text-gray-700 hover:bg-gray-100"
                    }`}
                  >
                    {day}
                  </button>
                );
              })}
            </div>
          </div>

          {/* 회의실 목록 */}
          <div className="bg-white rounded-lg shadow p-3">
            <p className="text-xs font-semibold text-gray-400 mb-2 uppercase tracking-wide">회의실</p>
            <div className="flex flex-col gap-2">
              {rooms.map((room) => (
                <div key={room.roomId} className="flex items-center gap-2 text-xs">
                  <span className={`w-2 h-2 rounded-full flex-shrink-0 ${
                    room.status !== "ACTIVE" ? "bg-gray-300"
                    : isRoomFullyBooked(room.roomId) ? "bg-red-400"
                    : "bg-green-400"
                  }`} />
                  <div>
                    <p className="text-gray-700 font-medium">{room.roomName}</p>
                    {room.location && <p className="text-gray-400 text-[10px]">{room.location}</p>}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 범례 */}
          <div className="bg-white rounded-lg shadow p-3">
            <p className="text-xs font-semibold text-gray-400 mb-2 uppercase tracking-wide">범례</p>
            <div className="flex flex-col gap-1.5 text-[11px]">
              <div className="flex items-center gap-2">
                <div className="w-8 h-4 bg-blue-100 rounded-sm flex items-center px-0.5">
                  <div className="w-full h-2.5 bg-blue-500 rounded-sm" />
                </div>
                <span className="text-gray-600">예약됨</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-8 h-4 bg-white border border-gray-200 rounded-sm" />
                <span className="text-gray-600">예약 가능</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-8 h-4 bg-gray-100 rounded-sm" />
                <span className="text-gray-600">사용 불가</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-red-400 inline-block" />
                <span className="text-gray-600">예약 마감</span>
              </div>
            </div>
          </div>
        </div>

        {/* 오른쪽: 타임라인 or 내 예약 */}
        <div className="flex-1 bg-white rounded-lg shadow overflow-auto">
          {tab === "all" ? (
            <div className="p-4">
              <div className="flex items-center justify-between mb-3">
                <p className="text-sm font-semibold text-gray-700">{selectedDate} 예약 현황</p>
                <button
                  onClick={() => { setForm({ ...INIT_FORM, title: userInfo?.departmentName || "" }); setError(""); setShowModal(true); }}
                  className="px-3 py-1.5 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  + 예약하기
                </button>
              </div>
              <div className="overflow-x-auto">
                <table className="text-xs border-collapse" style={{ minWidth: "700px", width: "100%" }}>
                  <thead>
                    <tr>
                      <th className="text-left p-2 border-b border-r border-gray-200 text-gray-500 font-medium bg-gray-50 w-24 whitespace-nowrap">
                        회의실
                      </th>
                      {HOURS.map((h) => (
                        <th key={h} className="text-center px-1 py-2 border-b border-r border-gray-200 text-gray-400 font-normal bg-gray-50 whitespace-nowrap" style={{ minWidth: "52px" }}>
                          {String(h).padStart(2, "0")}:00
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {rooms.map((room) => (
                      <tr key={room.roomId} className="border-b border-gray-100">
                        <td className="p-2 border-r border-gray-200 font-medium text-gray-700 whitespace-nowrap">
                          {room.roomName}
                        </td>
                        {HOURS.map((hour) => {
                          const slot = getSlotInfo(room.roomId, hour);
                          const inactive = room.status !== "ACTIVE";
                          return (
                            <td
                              key={hour}
                              onClick={() => {
                                if (slot) setDetailReservation(slot);
                                else if (!inactive) handleSlotClick(room.roomId, hour);
                              }}
                              title={
                                slot ? `${slot.title} (${slot.userName}) ${slot.startTime}~${slot.endTime} — 클릭하여 상세보기`
                                : inactive ? "사용 불가"
                                : "클릭하여 예약"
                              }
                              className={`border-r border-gray-100 h-10 transition-colors ${
                                inactive ? "bg-gray-100 cursor-not-allowed"
                                : slot ? "bg-blue-100 cursor-pointer hover:bg-blue-200"
                                : "hover:bg-green-50 cursor-pointer"
                              }`}
                            >
                              {slot && (
                                <div className="px-1 bg-blue-500 text-white rounded text-[10px] mx-0.5 py-0.5 truncate">
                                  {slot.title}
                                </div>
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    ))}
                    {rooms.length === 0 && (
                      <tr>
                        <td colSpan={HOURS.length + 1} className="text-center py-8 text-gray-400 text-sm">
                          회의실 정보를 불러오는 중...
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
              <p className="text-xs text-gray-400 mt-2">* 빈 슬롯을 클릭하면 예약할 수 있어요</p>
            </div>
          ) : (
            <div className="p-4">
              <p className="text-sm font-semibold text-gray-700 mb-3">내 예약 목록</p>
              {myReservations.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-16 text-gray-400">
                  <p className="text-sm">예약된 회의실이 없어요</p>
                  <button
                    onClick={() => { setTab("all"); setForm({ ...INIT_FORM, title: userInfo?.departmentName || "" }); setError(""); setShowModal(true); }}
                    className="mt-3 px-4 py-2 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
                  >
                    + 예약하기
                  </button>
                </div>
              ) : (
                <div className="flex flex-col gap-2">
                  {myReservations.map((r) => (
                    <div
                      key={r.reservationId}
                      onClick={() => setDetailReservation(r)}
                      className="flex items-center justify-between p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer"
                    >
                      <div>
                        <p className="text-sm font-medium text-gray-800">{r.title}</p>
                        <p className="text-xs text-gray-500 mt-0.5">
                          {r.rsvDate} · {r.roomName} · {r.startTime} ~ {r.endTime}
                        </p>
                        {r.purpose && <p className="text-xs text-gray-400 mt-0.5 line-clamp-1">{r.purpose}</p>}
                      </div>
                      <div className="flex items-center gap-2 flex-shrink-0 ml-4">
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                          r.status === "CONFIRMED" ? "bg-blue-100 text-blue-700" : "bg-gray-100 text-gray-500"
                        }`}>
                          {r.status === "CONFIRMED" ? "예약됨" : "취소됨"}
                        </span>
                        {r.status === "CONFIRMED" && (
                          <button
                            onClick={(e) => { e.stopPropagation(); handleCancel(r.reservationId); }}
                            className="text-xs text-red-500 hover:text-red-700 border border-red-200 px-2 py-0.5 rounded hover:bg-red-50"
                          >
                            취소
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* 예약 상세 모달 */}
      {detailReservation && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96 shadow-xl">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-800">예약 상세</h2>
              <button
                onClick={() => setDetailReservation(null)}
                className="text-gray-400 hover:text-gray-600 text-xl leading-none"
              >
                ×
              </button>
            </div>
            <div className="flex flex-col gap-3">
              <div className="flex items-center gap-2">
                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                  detailReservation.status === "CONFIRMED" ? "bg-blue-100 text-blue-700" : "bg-gray-100 text-gray-500"
                }`}>
                  {detailReservation.status === "CONFIRMED" ? "예약됨" : "취소됨"}
                </span>
              </div>
              <div className="grid grid-cols-[80px_1fr] gap-y-2.5 text-sm">
                <span className="text-gray-400">회의실</span>
                <span className="text-gray-800 font-medium">{detailReservation.roomName}</span>
                <span className="text-gray-400">팀명</span>
                <span className="text-gray-800">{detailReservation.title}</span>
                <span className="text-gray-400">날짜</span>
                <span className="text-gray-800">{detailReservation.rsvDate}</span>
                <span className="text-gray-400">시간</span>
                <span className="text-gray-800">{detailReservation.startTime} ~ {detailReservation.endTime}</span>
                <span className="text-gray-400">예약자</span>
                <span className="text-gray-800">{detailReservation.userName || "-"}</span>
                {detailReservation.purpose && (
                  <>
                    <span className="text-gray-400">회의 목적</span>
                    <span className="text-gray-800 whitespace-pre-wrap">{detailReservation.purpose}</span>
                  </>
                )}
              </div>
            </div>
            <div className="flex justify-end gap-2 mt-6">
              {detailReservation.status === "CONFIRMED" && (
                <button
                  onClick={() => { handleCancel(detailReservation.reservationId); setDetailReservation(null); }}
                  className="px-4 py-2 text-sm text-red-500 border border-red-200 rounded hover:bg-red-50"
                >
                  예약 취소
                </button>
              )}
              <button
                onClick={() => setDetailReservation(null)}
                className="px-4 py-2 text-sm border rounded hover:bg-gray-50"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 예약 모달 */}
      {showModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-96 shadow-xl">
            <h2 className="text-lg font-semibold mb-4">회의실 예약</h2>
            <div className="flex flex-col gap-3">
              <div>
                <label className="text-sm text-gray-600">회의실</label>
                <select
                  value={form.roomId}
                  onChange={(e) => setForm((f) => ({ ...f, roomId: e.target.value }))}
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                >
                  <option value="">선택해주세요</option>
                  {rooms.filter((r) => r.status === "ACTIVE").map((r) => (
                    <option key={r.roomId} value={r.roomId}>{r.roomName}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="text-sm text-gray-600">날짜</label>
                <input
                  type="date"
                  value={selectedDate}
                  readOnly
                  className="w-full border rounded px-3 py-2 mt-1 text-sm bg-gray-50"
                />
              </div>
              <div className="flex gap-2">
                <div className="flex-1">
                  <label className="text-sm text-gray-600">시작 시간</label>
                  <select
                    value={form.startTime}
                    onChange={(e) => setForm((f) => ({ ...f, startTime: e.target.value }))}
                    className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  >
                    <option value="">선택</option>
                    {TIME_OPTIONS.map((t) => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
                <div className="flex-1">
                  <label className="text-sm text-gray-600">종료 시간</label>
                  <select
                    value={form.endTime}
                    onChange={(e) => setForm((f) => ({ ...f, endTime: e.target.value }))}
                    className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  >
                    <option value="">선택</option>
                    {TIME_OPTIONS.map((t) => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="text-sm text-gray-600">팀명</label>
                <input
                  value={form.title}
                  onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
                  type="text"
                  className="w-full border rounded px-3 py-2 mt-1 text-sm"
                  placeholder="팀명 입력"
                />
              </div>
              <div>
                <label className="text-sm text-gray-600">회의 목적</label>
                <textarea
                  value={form.purpose}
                  onChange={(e) => setForm((f) => ({ ...f, purpose: e.target.value }))}
                  rows={3}
                  className="w-full border rounded px-3 py-2 mt-1 text-sm resize-none"
                  placeholder="회의 목적을 입력해주세요"
                />
              </div>
              {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
            <div className="flex justify-end gap-2 mt-6">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 text-sm border rounded hover:bg-gray-50"
              >
                취소
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
              >
                {saving ? "저장 중..." : "예약"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
