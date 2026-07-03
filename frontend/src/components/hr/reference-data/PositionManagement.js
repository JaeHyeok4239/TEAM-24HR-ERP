"use client";

import { useEffect, useState } from "react";
import { Briefcase, Pencil, Plus } from "lucide-react";

import {
  createPositionRequest,
  getPositionsRequest,
  updatePositionRequest,
} from "@/services/referenceDataService";

const createInitialForm = () => ({
  positionCode: "",
  positionName: "",
  upperPositionId: "",
  isActive: "Y",
});

const createEmptyNotice = () => ({
  type: "",
  message: "",
});

const POSITION_ERROR_MESSAGES = {
  DUPLICATE_POSITION_CODE: "이미 사용 중인 직급 코드입니다.",
  DUPLICATE_POSITION_NAME: "이미 사용 중인 직급명입니다.",
  POSITION_HAS_ASSIGNED_EMPLOYEES:
    "해당 직급을 사용하는 직원이 있어 미사용 처리할 수 없습니다.\n직원의 직급을 먼저 변경해주세요.",
  POSITION_NOT_FOUND: "직급 정보를 찾을 수 없습니다.",
  INVALID_POSITION_ORDER: "직급 위치가 올바르지 않습니다.",
};

const CLOSE_MODAL_ERROR_CODES = ["POSITION_HAS_ASSIGNED_EMPLOYEES"];

export default function PositionManagement() {
  const [positions, setPositions] = useState([]);
  const [selectedPositionId, setSelectedPositionId] = useState(null);
  const [modalMode, setModalMode] = useState(null);
  const [form, setForm] = useState(createInitialForm);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [notice, setNotice] = useState(createEmptyNotice);
  const [modalError, setModalError] = useState("");

  const selectedPosition = positions.find(
    (position) => position.positionId === selectedPositionId,
  );

  const sortedPositions = [...positions].sort((first, second) => {
    const firstActive = first.isActive === "Y";
    const secondActive = second.isActive === "Y";

    if (firstActive !== secondActive) {
      return firstActive ? -1 : 1;
    }

    if (firstActive && secondActive) {
      const firstSortOrder = first.sortOrder ?? 0;
      const secondSortOrder = second.sortOrder ?? 0;

      if (firstSortOrder !== secondSortOrder) {
        return secondSortOrder - firstSortOrder;
      }
    }

    return first.positionId - second.positionId;
  });

  const activePositionCount = positions.filter(
    (position) => position.isActive === "Y",
  ).length;

  const inactivePositionCount = positions.length - activePositionCount;

  const activePositionOptions = positions
    .filter((position) => position.isActive === "Y")
    .filter((position) => position.positionId !== selectedPosition?.positionId)
    .sort((first, second) => {
      const firstSortOrder = first.sortOrder ?? 0;
      const secondSortOrder = second.sortOrder ?? 0;

      if (firstSortOrder !== secondSortOrder) {
        return secondSortOrder - firstSortOrder;
      }

      return first.positionId - second.positionId;
    });

  const refreshPositions = async () => {
    const response = await getPositionsRequest();

    setPositions(response);

    return response;
  };

  useEffect(() => {
    let cancelled = false;

    getPositionsRequest()
      .then((response) => {
        if (!cancelled) {
          setPositions(response);
        }
      })
      .catch((error) => {
        if (!cancelled) {
          setNotice({
            type: "error",
            message: error.message || "직급 목록을 불러오지 못했습니다.",
          });
        }
      })
      .finally(() => {
        if (!cancelled) {
          setIsLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const findUpperPositionId = (targetPosition) => {
    if (!targetPosition || targetPosition.isActive !== "Y") {
      return "";
    }

    const activePositionsAsc = positions
      .filter((position) => position.isActive === "Y")
      .sort((first, second) => {
        const firstSortOrder = first.sortOrder ?? 0;
        const secondSortOrder = second.sortOrder ?? 0;

        if (firstSortOrder !== secondSortOrder) {
          return firstSortOrder - secondSortOrder;
        }

        return first.positionId - second.positionId;
      });

    const targetIndex = activePositionsAsc.findIndex(
      (position) => position.positionId === targetPosition.positionId,
    );

    if (targetIndex < 0 || targetIndex === activePositionsAsc.length - 1) {
      return "";
    }

    return String(activePositionsAsc[targetIndex + 1].positionId);
  };

  const handleSelectPosition = (positionId) => {
    setSelectedPositionId(positionId);
    setNotice(createEmptyNotice());
  };

  const handleOpenCreateModal = () => {
    setForm(createInitialForm());
    setModalError("");
    setNotice(createEmptyNotice());
    setModalMode("create");
  };

  const handleOpenUpdateModal = () => {
    if (!selectedPosition) {
      return;
    }

    setForm({
      positionCode: selectedPosition.positionCode ?? "",
      positionName: selectedPosition.positionName ?? "",
      upperPositionId: findUpperPositionId(selectedPosition),
      isActive: selectedPosition.isActive ?? "Y",
    });

    setModalError("");
    setNotice(createEmptyNotice());
    setModalMode("update");
  };

  const handleCloseModal = () => {
    if (isSaving) {
      return;
    }

    setModalMode(null);
    setModalError("");
    setForm(createInitialForm());
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    let nextValue = value;

    if (name === "positionCode") {
      nextValue = value.toUpperCase();
    }

    setForm((previous) => ({
      ...previous,
      [name]: nextValue,
    }));

    setModalError("");
  };

  const parseUpperPositionId = () => {
    if (form.isActive === "N") {
      return null;
    }

    if (form.upperPositionId === "") {
      return null;
    }

    const upperPositionId = Number(form.upperPositionId);

    if (Number.isNaN(upperPositionId)) {
      throw new Error("바로 위 직급을 올바르게 선택해주세요.");
    }

    return upperPositionId;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const positionCode = form.positionCode.trim().toUpperCase();
    const positionName = form.positionName.trim();

    if (modalMode === "create" && !positionCode) {
      setModalError("직급 코드를 입력해주세요.");
      return;
    }

    if (modalMode === "create" && !/^[A-Z0-9_-]+$/.test(positionCode)) {
      setModalError("직급 코드는 대문자, 숫자, _, -만 사용할 수 있습니다.");
      return;
    }

    if (!positionName) {
      setModalError("직급명을 입력해주세요.");
      return;
    }

    if (modalMode === "update" && !["Y", "N"].includes(form.isActive)) {
      setModalError("사용 여부는 Y 또는 N만 가능합니다.");
      return;
    }

    if (
      modalMode === "update" &&
      form.upperPositionId !== "" &&
      Number(form.upperPositionId) === selectedPosition?.positionId
    ) {
      setModalError("자기 자신을 바로 위 직급으로 선택할 수 없습니다.");
      return;
    }

    const changingToInactive =
      modalMode === "update" &&
      selectedPosition?.isActive === "Y" &&
      form.isActive === "N";

    if (changingToInactive) {
      const confirmed = window.confirm(
        "선택한 직급을 미사용 처리하시겠습니까?\n" +
          "해당 직급을 사용하는 직원이 있으면 변경되지 않습니다.",
      );

      if (!confirmed) {
        return;
      }
    }

    let upperPositionId;

    try {
      upperPositionId = parseUpperPositionId();
    } catch (error) {
      setModalError(error.message);
      return;
    }

    try {
      setIsSaving(true);
      setModalError("");
      setNotice(createEmptyNotice());

      let savedPosition;

      if (modalMode === "create") {
        savedPosition = await createPositionRequest({
          positionCode,
          positionName,
          upperPositionId,
        });
      } else {
        savedPosition = await updatePositionRequest(
          selectedPosition.positionId,
          {
            positionName,
            isActive: form.isActive,
            upperPositionId,
          },
        );
      }

      await refreshPositions();

      setSelectedPositionId(savedPosition.positionId);

      setNotice({
        type: "success",
        message:
          modalMode === "create"
            ? "직급이 등록되었습니다."
            : "직급 정보가 수정되었습니다.",
      });

      setModalMode(null);
      setModalError("");
      setForm(createInitialForm());
    } catch (error) {
      const message =
        POSITION_ERROR_MESSAGES[error.code] ||
        error.message ||
        "직급 정보를 저장하지 못했습니다.";

      const shouldCloseModal = CLOSE_MODAL_ERROR_CODES.includes(error.code);

      if (shouldCloseModal) {
        setModalMode(null);
        setModalError("");
        setForm(createInitialForm());

        setNotice({
          type: "error",
          message,
        });
      } else {
        setModalError(message);
      }
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <>
      <section className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 px-5 py-4">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div className="flex min-w-0 items-start gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-[#eef7ff] text-[#1a2f4e]">
                <Briefcase size={21} strokeWidth={1.8} />
              </div>

              <div className="min-w-0">
                <h2 className="text-base font-bold text-slate-950">
                  직급 관리
                </h2>

                <p className="mt-1 text-xs leading-5 text-slate-500">
                  높은 직급부터 낮은 직급 순서로 관리합니다.
                </p>
              </div>
            </div>

            <div className="flex shrink-0 gap-2">
              <button
                type="button"
                onClick={handleOpenUpdateModal}
                disabled={!selectedPosition || isLoading}
                className="inline-flex h-9 items-center gap-1.5 rounded-md border border-slate-300 bg-white px-3 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                <Pencil size={15} />
                수정
              </button>

              <button
                type="button"
                onClick={handleOpenCreateModal}
                className="inline-flex h-9 items-center gap-1.5 rounded-md bg-[#1a2f4e] px-3 text-sm font-semibold text-white hover:bg-[#25476e]"
              >
                <Plus size={16} />
                직급 추가
              </button>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-3 border-b border-slate-100 bg-slate-50/70">
          <SummaryItem label="전체" value={positions.length} />
          <SummaryItem label="사용" value={activePositionCount} />
          <SummaryItem label="미사용" value={inactivePositionCount} />
        </div>

        <div className="px-5 py-4">
          {notice.message && <Notice notice={notice} />}

          <div className="overflow-hidden rounded-md border border-slate-200">
            <table className="w-full border-collapse text-sm">
              <thead className="bg-slate-50 text-slate-500">
                <tr>
                  <th className="w-[80px] border-b border-slate-200 px-4 py-3 text-left text-xs font-semibold">
                    순서
                  </th>
                  <th className="border-b border-slate-200 px-4 py-3 text-left text-xs font-semibold">
                    직급명
                  </th>
                  <th className="w-[190px] border-b border-slate-200 px-4 py-3 text-left text-xs font-semibold">
                    직급 코드
                  </th>
                  <th className="w-[110px] border-b border-slate-200 px-4 py-3 text-center text-xs font-semibold">
                    상태
                  </th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100">
                {isLoading && (
                  <EmptyRow colSpan={4} message="직급 목록을 불러오는 중입니다." />
                )}

                {!isLoading && sortedPositions.length === 0 && (
                  <EmptyRow colSpan={4} message="등록된 직급이 없습니다." />
                )}

                {!isLoading &&
                  sortedPositions.map((position, index) => {
                    const isSelected =
                      position.positionId === selectedPositionId;

                    const isActive = position.isActive === "Y";

                    return (
                      <tr
                        key={position.positionId}
                        onClick={() =>
                          handleSelectPosition(position.positionId)
                        }
                        className={[
                          "cursor-pointer transition-colors",
                          isSelected ? "bg-[#eef7ff]" : "hover:bg-slate-50",
                        ].join(" ")}
                      >
                        <td
                          className={[
                            "border-l-4 px-4 py-3",
                            isSelected
                              ? "border-[#1a2f4e]"
                              : "border-transparent",
                          ].join(" ")}
                        >
                          <span className="inline-flex h-6 min-w-6 items-center justify-center rounded-md bg-slate-100 px-2 text-xs font-bold text-slate-500">
                            {index + 1}
                          </span>
                        </td>

                        <td className="px-4 py-3">
                          <span
                            className={[
                              "font-semibold",
                              isActive ? "text-slate-900" : "text-slate-400",
                            ].join(" ")}
                          >
                            {position.positionName}
                          </span>
                        </td>

                        <td className="px-4 py-3">
                          <CodeBadge>{position.positionCode}</CodeBadge>
                        </td>

                        <td className="px-4 py-3 text-center">
                          <StatusBadge active={isActive} />
                        </td>
                      </tr>
                    );
                  })}
              </tbody>
            </table>
          </div>

          <p className="mt-3 text-xs text-slate-400">
            항목을 선택한 뒤 수정 버튼을 눌러 직급명, 사용 여부, 직급 위치를 변경할 수 있습니다.
          </p>
        </div>
      </section>

      {modalMode && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <form
            onSubmit={handleSubmit}
            noValidate
            className="w-full max-w-md overflow-hidden rounded-lg bg-white shadow-xl"
          >
            <div className="border-b border-slate-200 px-6 py-5">
              <h3 className="text-lg font-bold text-slate-950">
                {modalMode === "create" ? "직급 추가" : "직급 수정"}
              </h3>

              <p className="mt-1 text-sm text-slate-500">
                직급 기준정보를 입력해주세요.
              </p>
            </div>

            <div className="px-6 py-5">
              {modalError && <ModalError message={modalError} />}

              <div className="space-y-4">
                <FormField label="직급 코드">
                  <input
                    type="text"
                    name="positionCode"
                    value={form.positionCode}
                    onChange={handleChange}
                    disabled={modalMode === "update"}
                    placeholder="예: MANAGER"
                    className={inputClassName}
                  />
                </FormField>

                <FormField label="직급명">
                  <input
                    type="text"
                    name="positionName"
                    value={form.positionName}
                    onChange={handleChange}
                    placeholder="예: 과장"
                    className={inputClassName}
                  />
                </FormField>

                {modalMode === "update" && (
                  <FormField label="사용 여부">
                    <select
                      name="isActive"
                      value={form.isActive}
                      onChange={handleChange}
                      className={inputClassName}
                    >
                      <option value="Y">사용</option>
                      <option value="N">미사용</option>
                    </select>
                  </FormField>
                )}

                <FormField
                  label="바로 위 직급"
                  help="선택한 직급보다 한 단계 낮은 위치에 저장됩니다."
                >
                  <select
                    name="upperPositionId"
                    value={form.upperPositionId}
                    onChange={handleChange}
                    disabled={modalMode === "update" && form.isActive === "N"}
                    className={inputClassName}
                  >
                    <option value="">없음 - 최상위 직급</option>

                    {activePositionOptions.map((position) => (
                      <option
                        key={position.positionId}
                        value={position.positionId}
                      >
                        {position.positionName}
                      </option>
                    ))}
                  </select>
                </FormField>
              </div>
            </div>

            <div className="flex justify-end gap-2 border-t border-slate-200 bg-slate-50 px-6 py-4">
              <button
                type="button"
                onClick={handleCloseModal}
                disabled={isSaving}
                className="h-9 rounded-md border border-slate-300 bg-white px-5 text-sm text-slate-700 hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
              >
                취소
              </button>

              <button
                type="submit"
                disabled={isSaving}
                className="h-9 rounded-md bg-[#1a2f4e] px-6 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500"
              >
                {isSaving ? "저장 중..." : "저장"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}

const inputClassName =
  "h-9 w-full rounded-md border border-slate-200 bg-white px-3 text-sm outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500";

function SummaryItem({ label, value }) {
  return (
    <div className="border-r border-slate-100 px-5 py-3 last:border-r-0">
      <p className="text-xs font-medium text-slate-400">{label}</p>
      <p className="mt-1 text-lg font-bold text-slate-900">{value}</p>
    </div>
  );
}

function Notice({ notice }) {
  const isSuccess = notice.type === "success";

  return (
    <div
      role="alert"
      aria-live="polite"
      className={[
        "mb-4 whitespace-pre-line rounded-md border px-4 py-3 text-sm font-medium",
        isSuccess
          ? "border-emerald-200 bg-emerald-50 text-emerald-700"
          : "border-red-200 bg-red-50 text-red-700",
      ].join(" ")}
    >
      {notice.message}
    </div>
  );
}

function EmptyRow({ colSpan, message }) {
  return (
    <tr>
      <td colSpan={colSpan} className="px-4 py-10 text-center text-slate-500">
        {message}
      </td>
    </tr>
  );
}

function CodeBadge({ children }) {
  return (
    <span className="inline-flex rounded-md bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-600">
      {children || "-"}
    </span>
  );
}

function StatusBadge({ active }) {
  return (
    <span
      className={[
        "inline-flex rounded-full px-2.5 py-1 text-xs font-semibold",
        active
          ? "bg-emerald-50 text-emerald-700"
          : "bg-slate-100 text-slate-500",
      ].join(" ")}
    >
      {active ? "사용" : "미사용"}
    </span>
  );
}

function ModalError({ message }) {
  return (
    <div
      role="alert"
      aria-live="polite"
      className="mb-4 whitespace-pre-line rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700"
    >
      {message}
    </div>
  );
}

function FormField({ label, help, children }) {
  return (
    <div>
      <label className="mb-2 block text-sm font-semibold text-slate-700">
        {label}
      </label>

      {children}

      {help && <p className="mt-1.5 text-xs text-slate-400">{help}</p>}
    </div>
  );
}